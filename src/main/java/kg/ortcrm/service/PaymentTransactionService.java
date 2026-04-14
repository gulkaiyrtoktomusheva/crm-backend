package kg.ortcrm.service;

import kg.ortcrm.dto.paymenttransaction.PaymentAllocationRequest;
import kg.ortcrm.dto.paymenttransaction.PaymentAllocationResponse;
import kg.ortcrm.dto.paymenttransaction.PaymentTransactionRequest;
import kg.ortcrm.dto.paymenttransaction.PaymentTransactionResponse;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.PaymentAllocation;
import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.PaymentTransaction;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.PaymentAllocationRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentAgreementRepository paymentAgreementRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;
    private final PaymentAgreementService paymentAgreementService;
    private final PaymentScheduleStatusResolver statusResolver;

    public List<PaymentTransactionResponse> findByStudentId(Long studentId) {
        return paymentTransactionRepository.findByAgreementStudentCourseStudentIdOrderByPaidAtDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PaymentTransactionResponse create(PaymentTransactionRequest request) {
        PaymentAgreement agreement = paymentAgreementRepository.findById(request.getAgreementId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment agreement not found with id: " + request.getAgreementId()));

        List<PaymentSchedule> schedules = paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(agreement.getId());
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("Payment agreement has no schedules");
        }

        PaymentTransaction transaction = paymentTransactionRepository.save(PaymentTransaction.builder()
                .student(agreement.getStudentCourse().getStudent())
                .agreement(agreement)
                .amount(request.getAmount())
                .paidAt(request.getPaidAt() != null ? request.getPaidAt() : LocalDate.now())
                .method(request.getMethod())
                .comment(request.getComment())
                .build());

        List<PaymentAllocation> allocations = request.getAllocations() == null || request.getAllocations().isEmpty()
                ? buildAutomaticAllocations(transaction, schedules, request.getAmount())
                : buildManualAllocations(transaction, schedules, request.getAllocations(), request.getAmount());

        paymentAllocationRepository.saveAll(allocations);
        applyAllocations(allocations, transaction.getPaidAt());
        paymentAgreementService.updateAgreementStatus(agreement, schedules);

        return toResponse(transaction);
    }

    private List<PaymentAllocation> buildAutomaticAllocations(PaymentTransaction transaction,
                                                              List<PaymentSchedule> schedules,
                                                              BigDecimal totalAmount) {
        BigDecimal remaining = totalAmount;
        List<PaymentAllocation> allocations = new ArrayList<>();

        for (PaymentSchedule schedule : schedules.stream()
                .sorted(Comparator.comparing(PaymentSchedule::getDueDate).thenComparing(PaymentSchedule::getInstallmentNumber))
                .toList()) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal scheduleRemaining = statusResolver.remainingAmount(schedule);
            if (scheduleRemaining.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal allocatedAmount = remaining.min(scheduleRemaining);
            allocations.add(PaymentAllocation.builder()
                    .transaction(transaction)
                    .schedule(schedule)
                    .allocatedAmount(allocatedAmount)
                    .build());
            remaining = remaining.subtract(allocatedAmount);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds outstanding agreement balance");
        }

        return allocations;
    }

    private List<PaymentAllocation> buildManualAllocations(PaymentTransaction transaction,
                                                           List<PaymentSchedule> schedules,
                                                           List<PaymentAllocationRequest> requests,
                                                           BigDecimal totalAmount) {
        BigDecimal allocatedTotal = requests.stream()
                .map(PaymentAllocationRequest::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (allocatedTotal.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Allocated total must match payment amount");
        }

        Map<Long, PaymentSchedule> schedulesById = schedules.stream()
                .collect(Collectors.toMap(PaymentSchedule::getId, Function.identity()));

        List<PaymentAllocation> allocations = new ArrayList<>();
        for (PaymentAllocationRequest request : requests) {
            PaymentSchedule schedule = schedulesById.get(request.getScheduleId());
            if (schedule == null) {
                throw new IllegalArgumentException("Schedule does not belong to the payment agreement: " + request.getScheduleId());
            }
            if (request.getAllocatedAmount().compareTo(statusResolver.remainingAmount(schedule)) > 0) {
                throw new IllegalArgumentException("Allocated amount exceeds schedule remaining amount");
            }
            allocations.add(PaymentAllocation.builder()
                    .transaction(transaction)
                    .schedule(schedule)
                    .allocatedAmount(request.getAllocatedAmount())
                    .build());
        }
        return allocations;
    }

    private void applyAllocations(List<PaymentAllocation> allocations, LocalDate paidAt) {
        List<PaymentSchedule> schedulesToUpdate = new ArrayList<>();
        for (PaymentAllocation allocation : allocations) {
            PaymentSchedule schedule = allocation.getSchedule();
            schedule.setPaidAmount(schedule.getPaidAmount().add(allocation.getAllocatedAmount()));
            paymentAgreementService.refreshScheduleState(schedule);
            if (statusResolver.resolve(schedule).name().equals("PAID")) {
                schedule.setPaidDate(paidAt);
            }
            schedulesToUpdate.add(schedule);
        }
        paymentScheduleRepository.saveAll(schedulesToUpdate);
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        List<PaymentAllocationResponse> allocations = paymentAllocationRepository.findByTransactionIdOrderByIdAsc(transaction.getId()).stream()
                .map(allocation -> PaymentAllocationResponse.builder()
                        .scheduleId(allocation.getSchedule().getId())
                        .installmentNumber(allocation.getSchedule().getInstallmentNumber())
                        .allocatedAmount(allocation.getAllocatedAmount())
                        .build())
                .toList();

        return PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .studentId(transaction.getStudent().getId())
                .studentName(transaction.getStudent().getFullName())
                .agreementId(transaction.getAgreement().getId())
                .amount(transaction.getAmount())
                .paidAt(transaction.getPaidAt())
                .method(transaction.getMethod())
                .comment(transaction.getComment())
                .createdAt(transaction.getCreatedAt())
                .allocations(allocations)
                .build();
    }
}

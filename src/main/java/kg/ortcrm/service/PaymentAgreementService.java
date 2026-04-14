package kg.ortcrm.service;

import kg.ortcrm.dto.paymentagreement.PaymentAgreementRequest;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementResponse;
import kg.ortcrm.dto.paymentschedule.PaymentScheduleResponse;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import kg.ortcrm.entity.enums.PaymentScheduleStatus;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentAgreementService {

    private final PaymentAgreementRepository paymentAgreementRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final PaymentScheduleStatusResolver statusResolver;

    public PaymentAgreementResponse findById(Long id) {
        PaymentAgreement agreement = paymentAgreementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment agreement not found with id: " + id));
        return toResponse(agreement, paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(id));
    }

    public List<PaymentAgreementResponse> findByStudentId(Long studentId) {
        return paymentAgreementRepository.findByStudentCourseStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(agreement -> toResponse(
                        agreement,
                        paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(agreement.getId())))
                .toList();
    }

    @Transactional
    public PaymentAgreementResponse update(Long id, PaymentAgreementRequest request) {
        validateRequest(request);

        PaymentAgreement agreement = paymentAgreementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment agreement not found with id: " + id));
        if (!agreement.getStudentCourse().getId().equals(request.getStudentCourseId())) {
            throw new IllegalArgumentException("Payment agreement cannot be moved to another student course");
        }
        if (agreement.getStatus() != PaymentAgreementStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active payment agreements can be updated");
        }
        if (paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(id).stream()
                .anyMatch(schedule -> schedule.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("Cannot update payment agreement after payments have been applied");
        }

        agreement.setType(request.getType());
        agreement.setFirstDueDate(request.getFirstDueDate());
        agreement.setMonthsCount(request.getType() == PaymentAgreementType.FULL ? 1 : request.getMonthsCount());
        agreement.setBillingDay(resolveBillingDay(request));
        agreement.setTotalAmount(agreement.getStudentCourse().getFinalPrice());

        paymentScheduleRepository.deleteByAgreementId(id);
        List<PaymentSchedule> schedules = paymentScheduleRepository.saveAll(generateSchedules(agreement));
        PaymentAgreement saved = paymentAgreementRepository.save(agreement);
        return toResponse(saved, schedules);
    }

    @Transactional
    public PaymentAgreementResponse create(PaymentAgreementRequest request) {
        validateRequest(request);

        StudentCourse studentCourse = studentCourseRepository.findById(request.getStudentCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Student course not found with id: " + request.getStudentCourseId()));
        if (studentCourse.getStatus() == StudentCourseStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot create payment agreement for a cancelled student course");
        }

        if (paymentAgreementRepository.existsByStudentCourseIdAndStatusIn(
                request.getStudentCourseId(),
                EnumSet.of(PaymentAgreementStatus.ACTIVE))) {
            throw new IllegalArgumentException("An active payment agreement already exists for this student course");
        }

        PaymentAgreement agreement = PaymentAgreement.builder()
                .studentCourse(studentCourse)
                .type(request.getType())
                .totalAmount(studentCourse.getFinalPrice())
                .firstDueDate(request.getFirstDueDate())
                .monthsCount(request.getType() == PaymentAgreementType.FULL ? 1 : request.getMonthsCount())
                .billingDay(resolveBillingDay(request))
                .status(PaymentAgreementStatus.ACTIVE)
                .build();

        PaymentAgreement savedAgreement = paymentAgreementRepository.save(agreement);
        List<PaymentSchedule> schedules = paymentScheduleRepository.saveAll(generateSchedules(savedAgreement));

        return toResponse(savedAgreement, schedules);
    }

    @Transactional
    public PaymentAgreementResponse refreshStatus(Long id) {
        PaymentAgreement agreement = paymentAgreementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment agreement not found with id: " + id));
        List<PaymentSchedule> schedules = paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(id);
        updateAgreementStatus(agreement, schedules);
        return toResponse(agreement, schedules);
    }

    public void refreshScheduleState(PaymentSchedule schedule) {
        schedule.setStatus(statusResolver.resolve(schedule));
        if (schedule.getPaidAmount() != null && schedule.getPaidAmount().compareTo(schedule.getAmountDue()) >= 0) {
            if (schedule.getPaidDate() == null) {
                schedule.setPaidDate(LocalDate.now());
            }
        } else {
            schedule.setPaidDate(null);
        }
    }

    public void updateAgreementStatus(PaymentAgreement agreement, List<PaymentSchedule> schedules) {
        schedules.forEach(this::refreshScheduleState);
        paymentScheduleRepository.saveAll(schedules);

        boolean allPaid = schedules.stream()
                .allMatch(schedule -> statusResolver.resolve(schedule).name().equals("PAID"));
        agreement.setStatus(allPaid ? PaymentAgreementStatus.COMPLETED : PaymentAgreementStatus.ACTIVE);
        paymentAgreementRepository.save(agreement);
    }

    public PaymentAgreementResponse toResponse(PaymentAgreement agreement, List<PaymentSchedule> schedules) {
        BigDecimal totalPaid = schedules.stream()
                .map(PaymentSchedule::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAmount = agreement.getTotalAmount().subtract(totalPaid).max(BigDecimal.ZERO);

        return PaymentAgreementResponse.builder()
                .id(agreement.getId())
                .studentCourseId(agreement.getStudentCourse().getId())
                .studentId(agreement.getStudentCourse().getStudent().getId())
                .studentName(agreement.getStudentCourse().getStudent().getFullName())
                .courseId(agreement.getStudentCourse().getCourse().getId())
                .courseName(agreement.getStudentCourse().getCourse().getName())
                .type(agreement.getType())
                .totalAmount(agreement.getTotalAmount())
                .firstDueDate(agreement.getFirstDueDate())
                .monthsCount(agreement.getMonthsCount())
                .billingDay(agreement.getBillingDay())
                .status(agreement.getStatus())
                .totalPaid(totalPaid)
                .remainingAmount(remainingAmount)
                .createdAt(agreement.getCreatedAt())
                .schedules(schedules.stream().map(this::toScheduleResponse).toList())
                .build();
    }

    public PaymentScheduleResponse toScheduleResponse(PaymentSchedule schedule) {
        return PaymentScheduleResponse.builder()
                .id(schedule.getId())
                .installmentNumber(schedule.getInstallmentNumber())
                .amountDue(schedule.getAmountDue())
                .paidAmount(schedule.getPaidAmount())
                .remainingAmount(statusResolver.remainingAmount(schedule))
                .dueDate(schedule.getDueDate())
                .paidDate(schedule.getPaidDate())
                .status(statusResolver.resolve(schedule))
                .comment(schedule.getComment())
                .createdAt(schedule.getCreatedAt())
                .build();
    }

    private void validateRequest(PaymentAgreementRequest request) {
        if (request.getType() == PaymentAgreementType.FULL && request.getMonthsCount() != 1) {
            throw new IllegalArgumentException("Full payment agreement must have months count equal to 1");
        }
        if (request.getType() == PaymentAgreementType.INSTALLMENT && request.getMonthsCount() < 2) {
            throw new IllegalArgumentException("Installment agreement must span at least 2 months");
        }
        if (request.getBillingDay() != null && (request.getBillingDay() < 1 || request.getBillingDay() > 28)) {
            throw new IllegalArgumentException("Billing day must be between 1 and 28");
        }
    }

    private Integer resolveBillingDay(PaymentAgreementRequest request) {
        if (request.getType() == PaymentAgreementType.FULL) {
            return request.getFirstDueDate().getDayOfMonth();
        }
        return request.getBillingDay() != null ? request.getBillingDay() : request.getFirstDueDate().getDayOfMonth();
    }

    private List<PaymentSchedule> generateSchedules(PaymentAgreement agreement) {
        int installments = agreement.getMonthsCount();
        BigDecimal total = agreement.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal installmentAmount = total.divide(BigDecimal.valueOf(installments), 2, RoundingMode.DOWN);
        BigDecimal allocated = installmentAmount.multiply(BigDecimal.valueOf(installments - 1L));
        BigDecimal lastAmount = total.subtract(allocated).setScale(2, RoundingMode.HALF_UP);

        List<PaymentSchedule> schedules = new ArrayList<>();
        for (int i = 0; i < installments; i++) {
            PaymentSchedule schedule = PaymentSchedule.builder()
                    .agreement(agreement)
                    .installmentNumber(i + 1)
                    .amountDue(i == installments - 1 ? lastAmount : installmentAmount)
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(calculateDueDate(agreement, i))
                    .status(PaymentScheduleStatus.PENDING)
                    .build();
            refreshScheduleState(schedule);
            schedules.add(schedule);
        }
        return schedules;
    }

    private LocalDate calculateDueDate(PaymentAgreement agreement, int installmentIndex) {
        if (installmentIndex == 0) {
            return agreement.getFirstDueDate();
        }
        LocalDate baseDate = agreement.getFirstDueDate().plusMonths(installmentIndex);
        int dayOfMonth = agreement.getBillingDay() != null ? agreement.getBillingDay() : agreement.getFirstDueDate().getDayOfMonth();
        YearMonth yearMonth = YearMonth.from(baseDate);
        return yearMonth.atDay(Math.min(dayOfMonth, yearMonth.lengthOfMonth()));
    }
}

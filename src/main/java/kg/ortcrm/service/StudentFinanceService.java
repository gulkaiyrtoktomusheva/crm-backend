package kg.ortcrm.service;

import kg.ortcrm.dto.finance.StudentFinanceCourseResponse;
import kg.ortcrm.dto.finance.StudentFinanceResponse;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementResponse;
import kg.ortcrm.dto.paymenttransaction.PaymentTransactionResponse;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.PaymentTransactionRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import kg.ortcrm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentFinanceService {

    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final PaymentAgreementRepository paymentAgreementRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentAgreementService paymentAgreementService;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentScheduleStatusResolver statusResolver;

    @Transactional(readOnly = true)
    public StudentFinanceResponse getFinance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<StudentCourse> studentCourses = studentCourseRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        List<PaymentAgreement> agreements = paymentAgreementRepository.findByStudentCourseStudentIdOrderByCreatedAtDesc(studentId);
        Map<Long, PaymentAgreement> agreementsByCourseId = agreements.stream()
                .collect(Collectors.toMap(
                        agreement -> agreement.getStudentCourse().getId(),
                        Function.identity(),
                        (left, right) -> left));

        Map<Long, List<PaymentSchedule>> schedulesByAgreementId = agreements.stream()
                .collect(Collectors.toMap(
                        PaymentAgreement::getId,
                        agreement -> paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(agreement.getId())));

        List<StudentFinanceCourseResponse> courses = studentCourses.stream()
                .map(studentCourse -> toCourseResponse(studentCourse, agreementsByCourseId.get(studentCourse.getId()), schedulesByAgreementId))
                .toList();

        BigDecimal totalCourseAmount = studentCourses.stream()
                .filter(studentCourse -> studentCourse.getStatus() != StudentCourseStatus.CANCELLED)
                .map(StudentCourse::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = paymentTransactionRepository.sumPaidAmountByStudentId(studentId);
        BigDecimal balance = totalCourseAmount.subtract(totalPaid).max(BigDecimal.ZERO);

        List<PaymentSchedule> openSchedules = paymentScheduleRepository.findOpenByStudentId(studentId);
        BigDecimal overdueAmount = openSchedules.stream()
                .filter(schedule -> schedule.getDueDate().isBefore(LocalDate.now()))
                .map(statusResolver::remainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Optional<PaymentSchedule> nextDue = openSchedules.stream()
                .filter(schedule -> !schedule.getDueDate().isBefore(LocalDate.now()))
                .min(Comparator.comparing(PaymentSchedule::getDueDate).thenComparing(PaymentSchedule::getInstallmentNumber));

        List<PaymentTransactionResponse> transactions = paymentTransactionService.findByStudentId(studentId);

        return StudentFinanceResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .totalCourseAmount(totalCourseAmount)
                .totalPaid(totalPaid)
                .balance(balance)
                .overdueAmount(overdueAmount)
                .nextDueDate(nextDue.map(PaymentSchedule::getDueDate).orElse(null))
                .nextDueAmount(nextDue.map(statusResolver::remainingAmount).orElse(BigDecimal.ZERO))
                .courses(courses)
                .transactions(transactions)
                .build();
    }

    private StudentFinanceCourseResponse toCourseResponse(StudentCourse studentCourse,
                                                          PaymentAgreement agreement,
                                                          Map<Long, List<PaymentSchedule>> schedulesByAgreementId) {
        PaymentAgreementResponse agreementResponse = null;
        if (agreement != null) {
            agreementResponse = paymentAgreementService.toResponse(
                    agreement,
                    schedulesByAgreementId.getOrDefault(agreement.getId(), List.of()));
        }

        return StudentFinanceCourseResponse.builder()
                .studentCourseId(studentCourse.getId())
                .courseId(studentCourse.getCourse().getId())
                .courseName(studentCourse.getCourse().getName())
                .coursePrice(studentCourse.getCoursePrice())
                .discountAmount(studentCourse.getDiscountAmount())
                .finalPrice(studentCourse.getFinalPrice())
                .startDate(studentCourse.getCourse().getStartDate())
                .endDate(studentCourse.getCourse().getEndDate())
                .status(studentCourse.getStatus())
                .agreement(agreementResponse)
                .build();
    }
}

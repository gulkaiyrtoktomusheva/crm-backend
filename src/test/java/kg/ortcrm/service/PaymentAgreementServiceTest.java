package kg.ortcrm.service;

import kg.ortcrm.dto.paymentagreement.PaymentAgreementRequest;
import kg.ortcrm.dto.paymentagreement.PaymentAgreementResponse;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentAgreementServiceTest {

    @Mock private PaymentAgreementRepository paymentAgreementRepository;
    @Mock private PaymentScheduleRepository paymentScheduleRepository;
    @Mock private StudentCourseRepository studentCourseRepository;
    @Spy private PaymentScheduleStatusResolver statusResolver;

    @InjectMocks private PaymentAgreementService paymentAgreementService;

    @Test
    void createShouldGenerateInstallmentSchedule() {
        Student student = Student.builder().id(1L).fullName("A").build();
        Course course = Course.builder()
                .id(2L)
                .name("Math course")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .price(new BigDecimal("10000.00"))
                .build();
        StudentCourse studentCourse = StudentCourse.builder()
                .id(3L)
                .student(student)
                .course(course)
                .coursePrice(new BigDecimal("10000.00"))
                .discountAmount(BigDecimal.ZERO)
                .finalPrice(new BigDecimal("10000.00"))
                .status(StudentCourseStatus.ACTIVE)
                .build();
        PaymentAgreement savedAgreement = PaymentAgreement.builder()
                .id(4L)
                .studentCourse(studentCourse)
                .type(PaymentAgreementType.INSTALLMENT)
                .totalAmount(new BigDecimal("10000.00"))
                .firstDueDate(LocalDate.of(2026, 5, 10))
                .monthsCount(4)
                .billingDay(10)
                .status(PaymentAgreementStatus.ACTIVE)
                .build();

        when(studentCourseRepository.findById(3L)).thenReturn(Optional.of(studentCourse));
        when(paymentAgreementRepository.existsByStudentCourseIdAndStatusIn(any(), any())).thenReturn(false);
        when(paymentAgreementRepository.save(any(PaymentAgreement.class))).thenReturn(savedAgreement);
        when(paymentScheduleRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentAgreementRequest request = PaymentAgreementRequest.builder()
                .studentCourseId(3L)
                .type(PaymentAgreementType.INSTALLMENT)
                .firstDueDate(LocalDate.of(2026, 5, 10))
                .monthsCount(4)
                .billingDay(10)
                .build();

        PaymentAgreementResponse response = paymentAgreementService.create(request);

        assertEquals(4, response.getSchedules().size());
        assertEquals(new BigDecimal("2500.00"), response.getSchedules().get(0).getAmountDue());
        assertEquals(LocalDate.of(2026, 8, 10), response.getSchedules().get(3).getDueDate());
    }

    @Test
    void createShouldRejectInvalidFullAgreement() {
        PaymentAgreementRequest request = PaymentAgreementRequest.builder()
                .studentCourseId(3L)
                .type(PaymentAgreementType.FULL)
                .firstDueDate(LocalDate.of(2026, 5, 10))
                .monthsCount(2)
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentAgreementService.create(request));
    }
}

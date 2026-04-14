package kg.ortcrm.service;

import kg.ortcrm.dto.paymenttransaction.PaymentTransactionRequest;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.PaymentAgreement;
import kg.ortcrm.entity.PaymentSchedule;
import kg.ortcrm.entity.PaymentTransaction;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import kg.ortcrm.entity.enums.PaymentMethod;
import kg.ortcrm.entity.enums.PaymentScheduleStatus;
import kg.ortcrm.repository.PaymentAgreementRepository;
import kg.ortcrm.repository.PaymentAllocationRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.PaymentTransactionRepository;
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
class PaymentTransactionServiceTest {

    @Mock private PaymentTransactionRepository paymentTransactionRepository;
    @Mock private PaymentAgreementRepository paymentAgreementRepository;
    @Mock private PaymentScheduleRepository paymentScheduleRepository;
    @Mock private PaymentAllocationRepository paymentAllocationRepository;
    @Mock private PaymentAgreementService paymentAgreementService;
    @Spy private PaymentScheduleStatusResolver statusResolver;

    @InjectMocks private PaymentTransactionService paymentTransactionService;

    @Test
    void createShouldAllocateAcrossSchedulesAutomatically() {
        Student student = Student.builder().id(1L).fullName("A").build();
        Course course = Course.builder().id(2L).name("Math course").build();
        StudentCourse studentCourse = StudentCourse.builder().id(3L).student(student).course(course).build();
        PaymentAgreement agreement = PaymentAgreement.builder()
                .id(4L)
                .studentCourse(studentCourse)
                .type(PaymentAgreementType.INSTALLMENT)
                .status(PaymentAgreementStatus.ACTIVE)
                .build();

        PaymentSchedule first = PaymentSchedule.builder()
                .id(11L)
                .agreement(agreement)
                .installmentNumber(1)
                .amountDue(new BigDecimal("2500.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2026, 5, 10))
                .status(PaymentScheduleStatus.PENDING)
                .build();
        PaymentSchedule second = PaymentSchedule.builder()
                .id(12L)
                .agreement(agreement)
                .installmentNumber(2)
                .amountDue(new BigDecimal("2500.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2026, 6, 10))
                .status(PaymentScheduleStatus.PENDING)
                .build();

        PaymentTransaction savedTransaction = PaymentTransaction.builder()
                .id(20L)
                .student(student)
                .agreement(agreement)
                .amount(new BigDecimal("3000.00"))
                .paidAt(LocalDate.of(2026, 5, 5))
                .method(PaymentMethod.CASH)
                .build();

        when(paymentAgreementRepository.findById(4L)).thenReturn(Optional.of(agreement));
        when(paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(4L)).thenReturn(List.of(first, second));
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(savedTransaction);
        when(paymentAllocationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentAllocationRepository.findByTransactionIdOrderByIdAsc(20L)).thenReturn(List.of());
        PaymentTransactionRequest request = PaymentTransactionRequest.builder()
                .agreementId(4L)
                .amount(new BigDecimal("3000.00"))
                .paidAt(LocalDate.of(2026, 5, 5))
                .method(PaymentMethod.CASH)
                .build();

        paymentTransactionService.create(request);

        assertEquals(new BigDecimal("2500.00"), first.getPaidAmount());
        assertEquals(new BigDecimal("500.00"), second.getPaidAmount());
    }

    @Test
    void createShouldRejectPaymentExceedingOutstandingBalance() {
        Student student = Student.builder().id(1L).fullName("A").build();
        Course course = Course.builder().id(2L).name("Math course").build();
        StudentCourse studentCourse = StudentCourse.builder().id(3L).student(student).course(course).build();
        PaymentAgreement agreement = PaymentAgreement.builder().id(4L).studentCourse(studentCourse).build();
        PaymentSchedule schedule = PaymentSchedule.builder()
                .id(11L)
                .agreement(agreement)
                .installmentNumber(1)
                .amountDue(new BigDecimal("1000.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2026, 5, 10))
                .build();

        when(paymentAgreementRepository.findById(4L)).thenReturn(Optional.of(agreement));
        when(paymentScheduleRepository.findByAgreementIdOrderByInstallmentNumberAsc(4L)).thenReturn(List.of(schedule));
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaymentTransactionRequest request = PaymentTransactionRequest.builder()
                .agreementId(4L)
                .amount(new BigDecimal("1500.00"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentTransactionService.create(request));
    }
}

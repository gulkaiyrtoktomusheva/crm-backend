package kg.ortcrm.service;

import kg.ortcrm.dto.payment.PaymentRequest;
import kg.ortcrm.entity.enums.PaymentStatus;
import kg.ortcrm.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private PaymentMapper paymentMapper;

    @InjectMocks private PaymentService paymentService;

    @Test
    void createShouldRejectAmountGreaterThanTotalDue() {
        PaymentRequest request = PaymentRequest.builder()
                .studentId(1L)
                .amount(new BigDecimal("15000"))
                .totalDue(new BigDecimal("12000"))
                .status(PaymentStatus.PENDING)
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentService.create(request));
    }

    @Test
    void createShouldRejectPaidWithoutPaidDate() {
        PaymentRequest request = PaymentRequest.builder()
                .studentId(1L)
                .amount(new BigDecimal("12000"))
                .totalDue(new BigDecimal("12000"))
                .status(PaymentStatus.PAID)
                .dueDate(LocalDate.of(2026, 4, 1))
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentService.create(request));
    }

    @Test
    void findAllShouldRejectInvalidDateRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.findAll(
                        LocalDate.of(2026, 4, 30),
                        LocalDate.of(2026, 4, 1),
                        PageRequest.of(0, 20))
        );
    }

    @Test
    void findAllShouldFilterByDueDateRange() {
        LocalDate dateFrom = LocalDate.of(2026, 4, 1);
        LocalDate dateTo = LocalDate.of(2026, 4, 30);
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Payment> payments = new PageImpl<>(List.of(Payment.builder().id(1L).build()));

        when(paymentRepository.findByDueDateBetween(dateFrom, dateTo, pageable)).thenReturn(payments);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(null);

        paymentService.findAll(dateFrom, dateTo, pageable);

        verify(paymentRepository).findByDueDateBetween(dateFrom, dateTo, pageable);
    }
}

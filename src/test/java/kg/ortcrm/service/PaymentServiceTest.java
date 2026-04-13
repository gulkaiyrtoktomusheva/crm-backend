package kg.ortcrm.service;

import kg.ortcrm.dto.payment.PaymentRequest;
import kg.ortcrm.entity.enums.PaymentStatus;
import kg.ortcrm.mapper.PaymentMapper;
import kg.ortcrm.repository.PaymentRepository;
import kg.ortcrm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}

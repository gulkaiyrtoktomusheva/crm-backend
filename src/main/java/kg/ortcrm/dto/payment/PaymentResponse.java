package kg.ortcrm.dto.payment;

import kg.ortcrm.entity.enums.PaymentMethod;
import kg.ortcrm.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private BigDecimal amount;
    private BigDecimal totalDue;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private Integer installmentNumber;
    private Integer totalInstallments;
    private String comment;
    private LocalDateTime createdAt;
}

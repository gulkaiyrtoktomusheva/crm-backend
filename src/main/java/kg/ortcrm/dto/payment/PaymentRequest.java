package kg.ortcrm.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.ortcrm.entity.enums.PaymentMethod;
import kg.ortcrm.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Total due is required")
    @Positive(message = "Total due must be positive")
    private BigDecimal totalDue;

    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private Integer installmentNumber;
    private Integer totalInstallments;
    private String comment;
}

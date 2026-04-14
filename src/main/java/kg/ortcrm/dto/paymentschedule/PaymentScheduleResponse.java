package kg.ortcrm.dto.paymentschedule;

import kg.ortcrm.entity.enums.PaymentScheduleStatus;
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
public class PaymentScheduleResponse {

    private Long id;
    private Integer installmentNumber;
    private BigDecimal amountDue;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private PaymentScheduleStatus status;
    private String comment;
    private LocalDateTime createdAt;
}

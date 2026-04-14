package kg.ortcrm.dto.paymenttransaction;

import kg.ortcrm.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long agreementId;
    private BigDecimal amount;
    private LocalDate paidAt;
    private PaymentMethod method;
    private String comment;
    private LocalDateTime createdAt;
    private List<PaymentAllocationResponse> allocations;
}

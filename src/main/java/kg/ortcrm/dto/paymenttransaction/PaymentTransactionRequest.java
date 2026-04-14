package kg.ortcrm.dto.paymenttransaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.ortcrm.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionRequest {

    @NotNull(message = "Agreement ID is required")
    private Long agreementId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDate paidAt;
    private PaymentMethod method;
    private String comment;

    @Valid
    private List<PaymentAllocationRequest> allocations;
}

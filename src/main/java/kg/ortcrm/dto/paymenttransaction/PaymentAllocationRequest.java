package kg.ortcrm.dto.paymenttransaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAllocationRequest {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Allocated amount is required")
    @Positive(message = "Allocated amount must be positive")
    private BigDecimal allocatedAmount;
}

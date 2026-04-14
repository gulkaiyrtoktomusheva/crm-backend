package kg.ortcrm.dto.paymenttransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAllocationResponse {

    private Long scheduleId;
    private Integer installmentNumber;
    private BigDecimal allocatedAmount;
}

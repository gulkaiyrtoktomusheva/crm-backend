package kg.ortcrm.dto.paymentagreement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.ortcrm.entity.enums.PaymentAgreementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAgreementRequest {

    @NotNull(message = "Student course ID is required")
    private Long studentCourseId;

    @NotNull(message = "Agreement type is required")
    private PaymentAgreementType type;

    @NotNull(message = "First due date is required")
    private LocalDate firstDueDate;

    @NotNull(message = "Months count is required")
    @Positive(message = "Months count must be positive")
    private Integer monthsCount;

    private Integer billingDay;
}

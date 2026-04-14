package kg.ortcrm.dto.paymentagreement;

import kg.ortcrm.dto.paymentschedule.PaymentScheduleResponse;
import kg.ortcrm.entity.enums.PaymentAgreementStatus;
import kg.ortcrm.entity.enums.PaymentAgreementType;
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
public class PaymentAgreementResponse {

    private Long id;
    private Long studentCourseId;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private PaymentAgreementType type;
    private BigDecimal totalAmount;
    private LocalDate firstDueDate;
    private Integer monthsCount;
    private Integer billingDay;
    private PaymentAgreementStatus status;
    private BigDecimal totalPaid;
    private BigDecimal remainingAmount;
    private LocalDateTime createdAt;
    private List<PaymentScheduleResponse> schedules;
}

package kg.ortcrm.dto.finance;

import kg.ortcrm.dto.paymentagreement.PaymentAgreementResponse;
import kg.ortcrm.entity.enums.StudentCourseStatus;
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
public class StudentFinanceCourseResponse {

    private Long studentCourseId;
    private Long courseId;
    private String courseName;
    private BigDecimal coursePrice;
    private BigDecimal discountAmount;
    private BigDecimal referralDiscountAmount;
    private BigDecimal finalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private StudentCourseStatus status;
    private PaymentAgreementResponse agreement;
}

package kg.ortcrm.dto.finance;

import kg.ortcrm.dto.paymenttransaction.PaymentTransactionResponse;
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
public class StudentFinanceResponse {

    private Long studentId;
    private String studentName;
    private BigDecimal totalCourseAmount;
    private BigDecimal totalPaid;
    private BigDecimal balance;
    private BigDecimal overdueAmount;
    private LocalDate nextDueDate;
    private BigDecimal nextDueAmount;
    private List<StudentFinanceCourseResponse> courses;
    private List<PaymentTransactionResponse> transactions;
}

package kg.ortcrm.dto.student;

import kg.ortcrm.dto.group.GroupResponse;
import kg.ortcrm.dto.mockexam.MockExamScoreResponse;
import kg.ortcrm.dto.payment.PaymentResponse;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.StudentStatus;
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
public class StudentDetailResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String whatsapp;
    private String school;
    private Integer grade;
    private String city;
    private String parentName;
    private String parentPhone;
    private LocalDate ortDate;
    private StudentStatus status;
    private LeadSource source;
    private String referredBy;
    private LocalDateTime createdAt;


    // Groups
    private List<GroupResponse> groups;

    // Attendance stats
    private Double attendancePercentage;
    private Long totalLessons;
    private Long attendedLessons;

    // Mock exam scores
    private List<MockExamScoreResponse> mockExamScores;
    private Double averageMockScore;

    // Payments
    private List<PaymentResponse> payments;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
    private BigDecimal balance;
}

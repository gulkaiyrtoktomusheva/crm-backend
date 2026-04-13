package kg.ortcrm.dto.dashboard;

import kg.ortcrm.dto.lead.LeadStatsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // Students
    private long totalStudents;
    private long activeStudents;
    private long atRiskStudents;
    private long droppedStudents;
    private long completedStudents;

    // Leads
    private LeadStatsResponse leadStats;

    // Groups
    private long totalGroups;

    // Payments
    private BigDecimal totalRevenue;
    private long pendingPayments;
    private long overduePayments;

    // Attendance
    private Double averageAttendance;

    // Mock Exams
    private Double averageMockScore;
    private long totalMockExams;
}

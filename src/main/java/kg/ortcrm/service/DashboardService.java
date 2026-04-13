package kg.ortcrm.service;

import kg.ortcrm.dto.dashboard.DashboardStatsResponse;
import kg.ortcrm.dto.lead.LeadStatsResponse;
import kg.ortcrm.entity.enums.PaymentStatus;
import kg.ortcrm.entity.enums.StudentStatus;
import kg.ortcrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final LeadRepository leadRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MockExamRepository mockExamRepository;
    private final MockExamScoreRepository mockExamScoreRepository;
    private final LeadService leadService;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        // Student stats
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long atRiskStudents = studentRepository.countByStatus(StudentStatus.AT_RISK);
        long droppedStudents = studentRepository.countByStatus(StudentStatus.DROPPED);
        long completedStudents = studentRepository.countByStatus(StudentStatus.COMPLETED);

        // Lead stats
        LeadStatsResponse leadStats = leadService.getStats();

        // Group stats
        long totalGroups = groupRepository.count();

        // Payment stats
        BigDecimal totalRevenue = paymentRepository.sumAllPaidAmount();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        long pendingPayments = paymentRepository.countByStatus(PaymentStatus.PENDING);
        long overduePayments = paymentRepository.countByStatus(PaymentStatus.OVERDUE);

        // Mock exam stats
        Double averageAttendance = attendanceRepository.findAverageAttendancePercentage();
        Double averageMockScore = mockExamScoreRepository.findAverageScore();
        long totalMockExams = mockExamRepository.count();

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .atRiskStudents(atRiskStudents)
                .droppedStudents(droppedStudents)
                .completedStudents(completedStudents)
                .leadStats(leadStats)
                .totalGroups(totalGroups)
                .totalRevenue(totalRevenue)
                .pendingPayments(pendingPayments)
                .overduePayments(overduePayments)
                .averageAttendance(averageAttendance)
                .averageMockScore(averageMockScore)
                .totalMockExams(totalMockExams)
                .build();
    }
}

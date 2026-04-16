package kg.ortcrm.service;

import kg.ortcrm.dto.dashboard.DashboardStatsResponse;
import kg.ortcrm.dto.lead.LeadStatsResponse;
import kg.ortcrm.entity.enums.StudentStatus;
import kg.ortcrm.repository.GroupRepository;
import kg.ortcrm.repository.LeadRepository;
import kg.ortcrm.repository.LessonAttendanceRepository;
import kg.ortcrm.repository.MockExamRepository;
import kg.ortcrm.repository.MockExamScoreRepository;
import kg.ortcrm.repository.PaymentScheduleRepository;
import kg.ortcrm.repository.PaymentTransactionRepository;
import kg.ortcrm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private LeadRepository leadRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private LessonAttendanceRepository lessonAttendanceRepository;
    @Mock private MockExamRepository mockExamRepository;
    @Mock private MockExamScoreRepository mockExamScoreRepository;
    @Mock private PaymentTransactionRepository paymentTransactionRepository;
    @Mock private PaymentScheduleRepository paymentScheduleRepository;
    @Mock private LeadService leadService;

    @InjectMocks private DashboardService dashboardService;

    @Test
    void getStatsShouldPopulateAverageFields() {
        when(studentRepository.count()).thenReturn(12L);
        when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(8L);
        when(studentRepository.countByStatus(StudentStatus.AT_RISK)).thenReturn(2L);
        when(studentRepository.countByStatus(StudentStatus.DROPPED)).thenReturn(1L);
        when(studentRepository.countByStatus(StudentStatus.COMPLETED)).thenReturn(1L);
        when(leadService.getStats()).thenReturn(new LeadStatsResponse());
        when(groupRepository.count()).thenReturn(5L);
        when(paymentTransactionRepository.sumAllPaidAmount()).thenReturn(new BigDecimal("120000"));
        when(paymentScheduleRepository.countPending(LocalDate.now())).thenReturn(3L);
        when(paymentScheduleRepository.countOverdue(LocalDate.now())).thenReturn(1L);
        when(lessonAttendanceRepository.findAverageAttendancePercentage()).thenReturn(87.5);
        when(mockExamScoreRepository.findAverageScore()).thenReturn(164.25);
        when(mockExamRepository.count()).thenReturn(4L);

        DashboardStatsResponse response = dashboardService.getStats();

        assertEquals(87.5, response.getAverageAttendance());
        assertEquals(164.25, response.getAverageMockScore());
        assertEquals(4L, response.getTotalMockExams());
    }
}

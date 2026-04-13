package kg.ortcrm.service;

import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.entity.enums.StudentStatus;
import kg.ortcrm.repository.AttendanceRepository;
import kg.ortcrm.repository.MockExamScoreRepository;
import kg.ortcrm.repository.PaymentRepository;
import kg.ortcrm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private AttendanceRepository attendanceRepository;
    @Mock private MockExamScoreRepository mockExamScoreRepository;
    @Mock private PaymentRepository paymentRepository;

    @InjectMocks private ReportService reportService;

    @Test
    void exportStudentsCsvShouldIncludeDerivedMetrics() {
        Subject subject = Subject.builder().id(1L).name("математика").build();
        Group group = Group.builder().id(2L).name("Математика A1").subject(subject).build();
        Student student = Student.builder()
                .id(5L)
                .fullName("Нурсултан Жолдошев")
                .phone("+996555000101")
                .school("№61 мектеп")
                .grade(11)
                .city("Бишкек")
                .status(StudentStatus.ACTIVE)
                .groups(new LinkedHashSet<>(List.of(group)))
                .build();

        when(studentRepository.findByFilters(eq(StudentStatus.ACTIVE), eq(1L), eq(2L), eq("Нурсултан"), eq(Pageable.unpaged())))
                .thenReturn(new PageImpl<>(List.of(student)));
        when(attendanceRepository.countTotalByStudentId(5L)).thenReturn(10L);
        when(attendanceRepository.countPresentByStudentId(5L)).thenReturn(8L);
        when(mockExamScoreRepository.findAverageScoreByStudentId(5L)).thenReturn(170.5);
        when(paymentRepository.sumPaidAmountByStudentId(5L)).thenReturn(new BigDecimal("9000"));
        when(paymentRepository.sumTotalDueByStudentId(5L)).thenReturn(new BigDecimal("12000"));

        String csv = new String(reportService.exportStudentsCsv(StudentStatus.ACTIVE, 1L, 2L, "Нурсултан"), StandardCharsets.UTF_8);

        assertTrue(csv.contains("fullName"));
        assertTrue(csv.contains("Нурсултан Жолдошев"));
        assertTrue(csv.contains("Математика A1"));
        assertTrue(csv.contains("математика"));
        assertTrue(csv.contains("\"80.00\""));
        assertTrue(csv.contains("\"170.50\""));
        assertTrue(csv.contains("\"3000\""));
    }
}

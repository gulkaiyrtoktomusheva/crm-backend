package kg.ortcrm.service;

import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.Student;
import kg.ortcrm.mapper.GroupMapper;
import kg.ortcrm.mapper.MockExamMapper;
import kg.ortcrm.mapper.StudentMapper;
import kg.ortcrm.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private LessonAttendanceRepository lessonAttendanceRepository;
    @Mock private MockExamScoreRepository mockExamScoreRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private StudentMapper studentMapper;
    @Mock private GroupMapper groupMapper;
    @Mock private MockExamMapper mockExamMapper;
    @Mock private PaymentMapper paymentMapper;
    @Mock private StudentFinanceService studentFinanceService;
    @Mock private ReferralService referralService;

    @InjectMocks private StudentService studentService;

    @Test
    void updateShouldChangeOnlyStudentFields() {
        Student student = Student.builder().id(10L).fullName("Old Name").build();
        StudentRequest request = StudentRequest.builder().fullName("New Name").build();
        StudentResponse response = StudentResponse.builder().id(10L).fullName("New Name").build();

        when(studentRepository.findById(10L)).thenReturn(java.util.Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(response);

        StudentResponse result = studentService.update(10L, request);

        assertEquals("New Name", result.getFullName());
    }
}

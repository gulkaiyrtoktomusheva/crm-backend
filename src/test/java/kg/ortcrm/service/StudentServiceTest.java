package kg.ortcrm.service;

import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.mapper.GroupMapper;
import kg.ortcrm.mapper.MockExamMapper;
import kg.ortcrm.mapper.PaymentMapper;
import kg.ortcrm.mapper.StudentMapper;
import kg.ortcrm.repository.AttendanceRepository;
import kg.ortcrm.repository.GroupRepository;
import kg.ortcrm.repository.MockExamScoreRepository;
import kg.ortcrm.repository.PaymentRepository;
import kg.ortcrm.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private AttendanceRepository attendanceRepository;
    @Mock private MockExamScoreRepository mockExamScoreRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private StudentMapper studentMapper;
    @Mock private GroupMapper groupMapper;
    @Mock private MockExamMapper mockExamMapper;
    @Mock private PaymentMapper paymentMapper;
    @Mock private GroupRepository groupRepository;

    @InjectMocks private StudentService studentService;

    @Test
    void updateShouldReplaceStudentGroups() {
        Group oldGroup = Group.builder().id(1L).name("Old").students(new LinkedHashSet<>()).build();
        Group newGroup = Group.builder().id(2L).name("New").students(new LinkedHashSet<>()).build();
        Student student = Student.builder().id(10L).groups(new LinkedHashSet<>(Set.of(oldGroup))).build();
        oldGroup.getStudents().add(student);

        StudentRequest request = StudentRequest.builder().groupIds(Set.of(2L)).build();
        StudentResponse response = StudentResponse.builder().id(10L).groupIds(Set.of(2L)).build();

        when(studentRepository.findById(10L)).thenReturn(java.util.Optional.of(student));
        when(groupRepository.findAllById(Set.of(2L))).thenReturn(List.of(newGroup));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(response);

        StudentResponse result = studentService.update(10L, request);

        assertEquals(Set.of(newGroup), student.getGroups());
        assertEquals(Set.of(student), newGroup.getStudents());
        assertEquals(Set.of(2L), result.getGroupIds());
    }

    @Test
    void updateShouldFailWhenGroupNotFound() {
        Student student = Student.builder().id(10L).groups(new LinkedHashSet<>()).build();
        StudentRequest request = StudentRequest.builder().groupIds(Set.of(99L)).build();

        when(studentRepository.findById(10L)).thenReturn(java.util.Optional.of(student));
        when(groupRepository.findAllById(Set.of(99L))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> studentService.update(10L, request));
    }
}

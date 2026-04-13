package kg.ortcrm.service;

import kg.ortcrm.dto.group.GroupResponse;
import kg.ortcrm.dto.mockexam.MockExamScoreResponse;
import kg.ortcrm.dto.payment.PaymentResponse;
import kg.ortcrm.dto.student.StudentDetailResponse;
import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.enums.StudentStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.GroupMapper;
import kg.ortcrm.mapper.MockExamMapper;
import kg.ortcrm.mapper.PaymentMapper;
import kg.ortcrm.mapper.StudentMapper;
import kg.ortcrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MockExamScoreRepository mockExamScoreRepository;
    private final PaymentRepository paymentRepository;
    private final StudentMapper studentMapper;
    private final GroupMapper groupMapper;
    private final MockExamMapper mockExamMapper;
    private final PaymentMapper paymentMapper;
    private final GroupRepository groupRepository;

    public Page<StudentResponse> findAll(StudentStatus status, Long groupId, Long id, String search, Pageable pageable) {
        String s  = (search == null) ? "" : search.trim();
        Page<Student> students = studentRepository.findByFilters(status, groupId, s, pageable);
        return students.map(studentMapper::toResponse);
    }

    public StudentResponse findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentDetailResponse findDetailById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));


        // Groups
        List<GroupResponse> groups = student.getGroups().stream()
                .map(groupMapper::toResponse)
                .collect(Collectors.toList());

        // Attendance stats
        long totalLessons = attendanceRepository.countTotalByStudentId(id);
        long attendedLessons = attendanceRepository.countPresentByStudentId(id);
        Double attendancePercentage = totalLessons > 0 ? (double) attendedLessons / totalLessons * 100 : null;

        // Mock exam scores
        List<MockExamScoreResponse> mockExamScores = mockExamMapper.toScoreResponseList(
                mockExamScoreRepository.findByStudentId(id));
        Double averageMockScore = mockExamScoreRepository.findAverageScoreByStudentId(id);

        // Payments
        List<PaymentResponse> payments = paymentMapper.toResponseList(
                paymentRepository.findByStudentIdOrderByDueDateAsc(id));
        BigDecimal totalPaid = paymentRepository.sumPaidAmountByStudentId(id);
        BigDecimal totalDue = paymentRepository.sumTotalDueByStudentId(id);

        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        if (totalDue == null) totalDue = BigDecimal.ZERO;

        BigDecimal balance = totalDue.subtract(totalPaid);

        return StudentDetailResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .phone(student.getPhone())
                .whatsapp(student.getWhatsapp())
                .school(student.getSchool())
                .grade(student.getGrade())
                .city(student.getCity())
                .parentName(student.getParentName())
                .parentPhone(student.getParentPhone())
                .ortDate(student.getOrtDate())
                .status(student.getStatus())
                .source(student.getSource())
                .referredBy(student.getReferredBy())
                .createdAt(student.getCreatedAt())
                .groups(groups)
                .attendancePercentage(attendancePercentage)
                .totalLessons(totalLessons)
                .attendedLessons(attendedLessons)
                .mockExamScores(mockExamScores)
                .averageMockScore(averageMockScore)
                .payments(payments)
                .totalPaid(totalPaid)
                .totalDue(totalDue)
                .balance(balance)
                .build();
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);

        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ACTIVE);
        }

        Student savedStudent = studentRepository.save(student);

        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            Set<Group> groups = new HashSet<>(groupRepository.findAllById(request.getGroupIds()));

            if (groups.size() != request.getGroupIds().size()) {
                throw new RuntimeException("Some groups not found");
            }

            savedStudent.getGroups().clear();
            savedStudent.getGroups().addAll(groups);

            for (Group g : groups) {
                g.getStudents().add(savedStudent);
            }

            savedStudent = studentRepository.save(savedStudent);
        }

        return studentMapper.toResponse(savedStudent);
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        studentMapper.updateEntity(student, request);


        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}

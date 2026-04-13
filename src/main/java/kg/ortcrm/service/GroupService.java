package kg.ortcrm.service;

import kg.ortcrm.dto.group.GroupDetailResponse;
import kg.ortcrm.dto.group.GroupRequest;
import kg.ortcrm.dto.group.GroupResponse;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.Group;
import kg.ortcrm.entity.Student;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.entity.User;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.mapper.GroupMapper;
import kg.ortcrm.mapper.StudentMapper;
import kg.ortcrm.repository.GroupRepository;
import kg.ortcrm.repository.StudentRepository;
import kg.ortcrm.repository.SubjectRepository;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;
    private final StudentMapper studentMapper;

    public List<GroupResponse> findAll(Long subjectId) {
        List<Group> groups;
        if (subjectId != null) {
            groups = groupRepository.findBySubjectId(subjectId);
        } else {
            groups = groupRepository.findAllWithDetails();
        }
        return groupMapper.toResponseList(groups);
    }

    public GroupResponse findById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        return groupMapper.toResponse(group);
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse findDetailById(Long id) {
        Group group = groupRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));

        List<StudentResponse> students = group.getStudents().stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());

        return GroupDetailResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .subjectId(group.getSubject().getId())
                .subjectName(group.getSubject().getName())
                .teacherId(group.getTeacher() != null ? group.getTeacher().getId() : null)
                .teacherName(group.getTeacher() != null ? group.getTeacher().getFullName() : null)
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .createdAt(group.getCreatedAt())
                .students(students)
                .build();
    }

    @Transactional
    public GroupResponse create(GroupRequest request) {
        Group group = groupMapper.toEntity(request);

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));
        group.setSubject(subject);

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getTeacherId()));
            group.setTeacher(teacher);
        }

        Group savedGroup = groupRepository.save(group);
        return groupMapper.toResponse(savedGroup);
    }

    @Transactional
    public GroupResponse update(Long id, GroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));

        groupMapper.updateEntity(group, request);

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));
            group.setSubject(subject);
        }

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getTeacherId()));
            group.setTeacher(teacher);
        }

        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toResponse(updatedGroup);
    }

    @Transactional
    public void delete(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }

    @Transactional
    public GroupResponse addStudent(Long groupId, Long studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        student.getGroups().add(group);
        studentRepository.save(student);

        return groupMapper.toResponse(group);
    }

    @Transactional
    public GroupResponse removeStudent(Long groupId, Long studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        student.getGroups().remove(group);
        studentRepository.save(student);

        return groupMapper.toResponse(group);
    }
}

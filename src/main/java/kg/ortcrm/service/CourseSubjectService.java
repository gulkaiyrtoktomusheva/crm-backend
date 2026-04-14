package kg.ortcrm.service;

import kg.ortcrm.dto.coursesubject.CourseSubjectRequest;
import kg.ortcrm.dto.coursesubject.CourseSubjectResponse;
import kg.ortcrm.entity.Course;
import kg.ortcrm.entity.CourseSubject;
import kg.ortcrm.entity.Role;
import kg.ortcrm.entity.Subject;
import kg.ortcrm.entity.User;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.CourseRepository;
import kg.ortcrm.repository.CourseSubjectRepository;
import kg.ortcrm.repository.SubjectRepository;
import kg.ortcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSubjectService {

    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public List<CourseSubjectResponse> findByCourseId(Long courseId) {
        return courseSubjectRepository.findByCourseIdOrderByCreatedAtAsc(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CourseSubjectResponse findById(Long id) {
        CourseSubject courseSubject = courseSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course subject not found with id: " + id));
        return toResponse(courseSubject);
    }

    @Transactional
    public CourseSubjectResponse create(Long courseId, CourseSubjectRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));
        User teacher = loadTeacher(request.getTeacherId());

        CourseSubject courseSubject = CourseSubject.builder()
                .course(course)
                .subject(subject)
                .teacher(teacher)
                .build();
        return toResponse(courseSubjectRepository.save(courseSubject));
    }

    @Transactional
    public CourseSubjectResponse update(Long id, CourseSubjectRequest request) {
        CourseSubject courseSubject = courseSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course subject not found with id: " + id));
        courseSubject.setSubject(subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId())));
        courseSubject.setTeacher(loadTeacher(request.getTeacherId()));
        return toResponse(courseSubjectRepository.save(courseSubject));
    }

    @Transactional
    public void delete(Long id) {
        if (!courseSubjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course subject not found with id: " + id);
        }
        courseSubjectRepository.deleteById(id);
    }

    private User loadTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teacherId));
        Role role = teacher.getRole();
        if (role == null || !"TEACHER".equalsIgnoreCase(role.getName())) {
            throw new IllegalArgumentException("Assigned user must have TEACHER role");
        }
        return teacher;
    }

    private CourseSubjectResponse toResponse(CourseSubject courseSubject) {
        return CourseSubjectResponse.builder()
                .id(courseSubject.getId())
                .courseId(courseSubject.getCourse().getId())
                .courseName(courseSubject.getCourse().getName())
                .subjectId(courseSubject.getSubject().getId())
                .subjectName(courseSubject.getSubject().getName())
                .teacherId(courseSubject.getTeacher().getId())
                .teacherName(courseSubject.getTeacher().getFullName())
                .createdAt(courseSubject.getCreatedAt())
                .build();
    }
}

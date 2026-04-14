package kg.ortcrm.service;

import kg.ortcrm.dto.lesson.LessonRequest;
import kg.ortcrm.dto.lesson.LessonResponse;
import kg.ortcrm.dto.lessonattendance.LessonAttendanceMarkRequest;
import kg.ortcrm.dto.lessonattendance.LessonAttendanceResponse;
import kg.ortcrm.entity.CourseSubject;
import kg.ortcrm.entity.Lesson;
import kg.ortcrm.entity.LessonAttendance;
import kg.ortcrm.entity.StudentCourse;
import kg.ortcrm.entity.enums.LessonStatus;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import kg.ortcrm.exception.ResourceNotFoundException;
import kg.ortcrm.repository.CourseSubjectRepository;
import kg.ortcrm.repository.LessonAttendanceRepository;
import kg.ortcrm.repository.LessonRepository;
import kg.ortcrm.repository.StudentCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseSubjectRepository courseSubjectRepository;
    private final LessonAttendanceRepository lessonAttendanceRepository;
    private final StudentCourseRepository studentCourseRepository;

    public List<LessonResponse> findByCourseId(Long courseId) {
        return lessonRepository.findByCourseSubjectCourseIdOrderByLessonDateAsc(courseId).stream()
                .map(this::toLessonResponse)
                .toList();
    }

    public List<LessonResponse> findByCourseSubjectId(Long courseSubjectId) {
        return lessonRepository.findByCourseSubjectIdOrderByLessonDateAsc(courseSubjectId).stream()
                .map(this::toLessonResponse)
                .toList();
    }

    public List<LessonAttendanceResponse> findAttendanceByLesson(Long lessonId) {
        return lessonAttendanceRepository.findByLessonIdOrderByStudentCourseStudentFullNameAsc(lessonId).stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    public List<LessonAttendanceResponse> findAttendanceByStudent(Long studentId) {
        return lessonAttendanceRepository.findByStudentCourseStudentIdOrderByLessonLessonDateAsc(studentId).stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    @Transactional
    public LessonResponse create(LessonRequest request) {
        CourseSubject courseSubject = courseSubjectRepository.findById(request.getCourseSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Course subject not found with id: " + request.getCourseSubjectId()));
        validateLesson(courseSubject, request);
        Lesson lesson = Lesson.builder()
                .courseSubject(courseSubject)
                .lessonDate(request.getLessonDate())
                .topic(request.getTopic())
                .status(request.getStatus() != null ? request.getStatus() : LessonStatus.SCHEDULED)
                .build();
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse update(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        CourseSubject courseSubject = courseSubjectRepository.findById(request.getCourseSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Course subject not found with id: " + request.getCourseSubjectId()));
        validateLesson(courseSubject, request);
        lesson.setCourseSubject(courseSubject);
        lesson.setLessonDate(request.getLessonDate());
        lesson.setTopic(request.getTopic());
        if (request.getStatus() != null) {
            lesson.setStatus(request.getStatus());
        }
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public List<LessonAttendanceResponse> markAttendance(Long lessonId, LessonAttendanceMarkRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        List<LessonAttendance> results = new ArrayList<>();
        for (LessonAttendanceMarkRequest.Record record : request.getRecords()) {
            StudentCourse studentCourse = studentCourseRepository.findById(record.getStudentCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student course not found with id: " + record.getStudentCourseId()));
            if (!studentCourse.getCourse().getId().equals(lesson.getCourseSubject().getCourse().getId())) {
                throw new IllegalArgumentException("Student course does not belong to the lesson course");
            }
            if (studentCourse.getStatus() == StudentCourseStatus.CANCELLED) {
                throw new IllegalArgumentException("Cannot mark attendance for cancelled student course");
            }

            LessonAttendance attendance = lessonAttendanceRepository.findByLessonIdAndStudentCourseId(lessonId, record.getStudentCourseId())
                    .orElse(LessonAttendance.builder()
                            .lesson(lesson)
                            .studentCourse(studentCourse)
                            .build());
            attendance.setPresent(record.getPresent());
            results.add(lessonAttendanceRepository.save(attendance));
        }
        return results.stream().map(this::toAttendanceResponse).toList();
    }

    private void validateLesson(CourseSubject courseSubject, LessonRequest request) {
        if (request.getLessonDate().isBefore(courseSubject.getCourse().getStartDate())
                || request.getLessonDate().isAfter(courseSubject.getCourse().getEndDate())) {
            throw new IllegalArgumentException("Lesson date must be within course dates");
        }
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .courseSubjectId(lesson.getCourseSubject().getId())
                .courseId(lesson.getCourseSubject().getCourse().getId())
                .courseName(lesson.getCourseSubject().getCourse().getName())
                .subjectId(lesson.getCourseSubject().getSubject().getId())
                .subjectName(lesson.getCourseSubject().getSubject().getName())
                .teacherId(lesson.getCourseSubject().getTeacher().getId())
                .teacherName(lesson.getCourseSubject().getTeacher().getFullName())
                .lessonDate(lesson.getLessonDate())
                .topic(lesson.getTopic())
                .status(lesson.getStatus())
                .createdAt(lesson.getCreatedAt())
                .build();
    }

    private LessonAttendanceResponse toAttendanceResponse(LessonAttendance attendance) {
        return LessonAttendanceResponse.builder()
                .id(attendance.getId())
                .lessonId(attendance.getLesson().getId())
                .lessonDate(attendance.getLesson().getLessonDate())
                .courseId(attendance.getLesson().getCourseSubject().getCourse().getId())
                .courseName(attendance.getLesson().getCourseSubject().getCourse().getName())
                .subjectId(attendance.getLesson().getCourseSubject().getSubject().getId())
                .subjectName(attendance.getLesson().getCourseSubject().getSubject().getName())
                .studentCourseId(attendance.getStudentCourse().getId())
                .studentId(attendance.getStudentCourse().getStudent().getId())
                .studentName(attendance.getStudentCourse().getStudent().getFullName())
                .present(attendance.getPresent())
                .createdAt(attendance.getCreatedAt())
                .build();
    }
}

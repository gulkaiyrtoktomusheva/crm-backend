package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.lesson.LessonRequest;
import kg.ortcrm.dto.lesson.LessonResponse;
import kg.ortcrm.dto.lessonattendance.LessonAttendanceMarkRequest;
import kg.ortcrm.dto.lessonattendance.LessonAttendanceResponse;
import kg.ortcrm.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Course lesson and attendance endpoints")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get lessons by course")
    public ResponseEntity<List<LessonResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.findByCourseId(courseId));
    }

    @GetMapping("/course-subject/{courseSubjectId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get lessons by course subject")
    public ResponseEntity<List<LessonResponse>> getByCourseSubject(@PathVariable Long courseSubjectId) {
        return ResponseEntity.ok(lessonService.findByCourseSubjectId(courseSubjectId));
    }

    @GetMapping("/{lessonId}/attendance")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get attendance by lesson")
    public ResponseEntity<List<LessonAttendanceResponse>> getAttendanceByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.findAttendanceByLesson(lessonId));
    }

    @GetMapping("/attendance/student/{studentId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get attendance by student")
    public ResponseEntity<List<LessonAttendanceResponse>> getAttendanceByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(lessonService.findAttendanceByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_MARK')")
    @Operation(summary = "Create lesson")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTENDANCE_MARK')")
    @Operation(summary = "Update lesson")
    public ResponseEntity<LessonResponse> update(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.update(id, request));
    }

    @PostMapping("/{lessonId}/attendance")
    @PreAuthorize("hasAuthority('ATTENDANCE_MARK')")
    @Operation(summary = "Mark lesson attendance")
    public ResponseEntity<List<LessonAttendanceResponse>> markAttendance(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonAttendanceMarkRequest request) {
        return ResponseEntity.ok(lessonService.markAttendance(lessonId, request));
    }
}

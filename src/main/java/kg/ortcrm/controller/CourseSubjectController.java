package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.coursesubject.CourseSubjectRequest;
import kg.ortcrm.dto.coursesubject.CourseSubjectResponse;
import kg.ortcrm.service.CourseSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-subjects")
@RequiredArgsConstructor
@Tag(name = "Course Subjects", description = "Subjects, teachers and schedule inside courses")
public class CourseSubjectController {

    private final CourseSubjectService courseSubjectService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('COURSE_VIEW')")
    @Operation(summary = "Get course subjects for course")
    public ResponseEntity<List<CourseSubjectResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseSubjectService.findByCourseId(courseId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_VIEW')")
    @Operation(summary = "Get course subject by ID")
    public ResponseEntity<CourseSubjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseSubjectService.findById(id));
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('COURSE_MANAGE_SUBJECTS')")
    @Operation(summary = "Add subject to course")
    public ResponseEntity<CourseSubjectResponse> create(@PathVariable Long courseId, @Valid @RequestBody CourseSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseSubjectService.create(courseId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_MANAGE_SUBJECTS')")
    @Operation(summary = "Update course subject")
    public ResponseEntity<CourseSubjectResponse> update(@PathVariable Long id, @Valid @RequestBody CourseSubjectRequest request) {
        return ResponseEntity.ok(courseSubjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_MANAGE_SUBJECTS')")
    @Operation(summary = "Delete course subject")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseSubjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.studentcourse.StudentCourseRequest;
import kg.ortcrm.dto.studentcourse.StudentCourseResponse;
import kg.ortcrm.service.StudentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-courses")
@RequiredArgsConstructor
@Tag(name = "Student Courses", description = "Student enrollments with course pricing")
public class StudentCourseController {

    private final StudentCourseService studentCourseService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @Operation(summary = "Get student course enrollments")
    public ResponseEntity<List<StudentCourseResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentCourseService.findByStudentId(studentId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @Operation(summary = "Get student course enrollment by ID")
    public ResponseEntity<StudentCourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentCourseService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @Operation(summary = "Create student course enrollment")
    public ResponseEntity<StudentCourseResponse> create(@Valid @RequestBody StudentCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCourseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @Operation(summary = "Update student course enrollment")
    public ResponseEntity<StudentCourseResponse> update(@PathVariable Long id, @Valid @RequestBody StudentCourseRequest request) {
        return ResponseEntity.ok(studentCourseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @Operation(summary = "Delete student course enrollment")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentCourseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

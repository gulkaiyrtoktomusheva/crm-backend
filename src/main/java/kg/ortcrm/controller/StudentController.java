package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.finance.StudentFinanceResponse;
import kg.ortcrm.dto.student.StudentDetailResponse;
import kg.ortcrm.dto.student.StudentRequest;
import kg.ortcrm.dto.student.StudentResponse;
import kg.ortcrm.entity.enums.StudentStatus;
import kg.ortcrm.service.StudentService;
import kg.ortcrm.service.StudentFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management endpoints")
public class StudentController {

    private final StudentService studentService;
    private final StudentFinanceService studentFinanceService;

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @Operation(summary = "Get all students", description = "Get paginated list of students with optional filters")
    public ResponseEntity<Page<StudentResponse>> getAll(
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(status, subjectId, groupId, search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @Operation(summary = "Get student 360° view", description = "Get complete student profile with subjects, groups, attendance, scores, and payments")
    public ResponseEntity<StudentDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findDetailById(id));
    }

    @GetMapping("/{id}/finance")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @Operation(summary = "Get student finance view")
    public ResponseEntity<StudentFinanceResponse> getFinance(@PathVariable Long id) {
        return ResponseEntity.ok(studentFinanceService.getFinance(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    @Operation(summary = "Create new student")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @Operation(summary = "Update student")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    @Operation(summary = "Delete student")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

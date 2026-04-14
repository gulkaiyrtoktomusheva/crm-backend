package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.mockexam.MockExamRequest;
import kg.ortcrm.dto.mockexam.MockExamResponse;
import kg.ortcrm.dto.mockexam.MockExamScoreRequest;
import kg.ortcrm.dto.mockexam.MockExamScoreResponse;
import kg.ortcrm.service.MockExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-exams")
@RequiredArgsConstructor
@Tag(name = "Mock Exams", description = "Mock exam management endpoints")
public class MockExamController {

    private final MockExamService mockExamService;

    @GetMapping
    @PreAuthorize("hasAuthority('MOCK_EXAM_VIEW')")
    @Operation(summary = "Get all mock exams")
    public ResponseEntity<List<MockExamResponse>> getAll() {
        return ResponseEntity.ok(mockExamService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MOCK_EXAM_VIEW')")
    @Operation(summary = "Get mock exam by ID", description = "Get mock exam details with all scores")
    public ResponseEntity<MockExamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mockExamService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MOCK_EXAM_CREATE')")
    @Operation(summary = "Create new mock exam")
    public ResponseEntity<MockExamResponse> create(@Valid @RequestBody MockExamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mockExamService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MOCK_EXAM_UPDATE')")
    @Operation(summary = "Update mock exam")
    public ResponseEntity<MockExamResponse> update(@PathVariable Long id, @Valid @RequestBody MockExamRequest request) {
        return ResponseEntity.ok(mockExamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MOCK_EXAM_DELETE')")
    @Operation(summary = "Delete mock exam")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mockExamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/scores")
    @PreAuthorize("hasAuthority('MOCK_EXAM_SCORE_MANAGE')")
    @Operation(summary = "Add scores to mock exam", description = "Bulk add or update scores for a mock exam")
    public ResponseEntity<List<MockExamScoreResponse>> addScores(
            @PathVariable Long id,
            @Valid @RequestBody MockExamScoreRequest request) {
        return ResponseEntity.ok(mockExamService.addScores(id, request));
    }

    @GetMapping("/{id}/scores")
    @PreAuthorize("hasAuthority('MOCK_EXAM_VIEW')")
    @Operation(summary = "Get scores for mock exam")
    public ResponseEntity<List<MockExamScoreResponse>> getScores(@PathVariable Long id) {
        return ResponseEntity.ok(mockExamService.findScoresByMockExamId(id));
    }

    @GetMapping("/student/{studentId}/scores")
    @PreAuthorize("hasAuthority('MOCK_EXAM_VIEW')")
    @Operation(summary = "Get all mock exam scores for student")
    public ResponseEntity<List<MockExamScoreResponse>> getScoresByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(mockExamService.findScoresByStudentId(studentId));
    }
}

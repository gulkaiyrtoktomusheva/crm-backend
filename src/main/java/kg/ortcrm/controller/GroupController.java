package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.group.GroupDetailResponse;
import kg.ortcrm.dto.group.GroupRequest;
import kg.ortcrm.dto.group.GroupResponse;
import kg.ortcrm.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    @PreAuthorize("hasAuthority('GROUP_VIEW')")
    @Operation(summary = "Get all groups", description = "Get list of groups with optional subject filter")
    public ResponseEntity<List<GroupResponse>> getAll(@RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(groupService.findAll(subjectId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GROUP_VIEW')")
    @Operation(summary = "Get group with students", description = "Get group details including list of students")
    public ResponseEntity<GroupDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.findDetailById(id));
    }

    @PostMapping({"", "/new"})
    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    @Operation(summary = "Create new group")
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GROUP_UPDATE')")
    @Operation(summary = "Update group")
    public ResponseEntity<GroupResponse> update(@PathVariable Long id, @Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GROUP_DELETE')")
    @Operation(summary = "Delete group")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAuthority('GROUP_MANAGE_STUDENTS')")
    @Operation(summary = "Add student to group")
    public ResponseEntity<GroupResponse> addStudent(@PathVariable Long id, @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.addStudent(id, studentId));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAuthority('GROUP_MANAGE_STUDENTS')")
    @Operation(summary = "Remove student from group")
    public ResponseEntity<GroupResponse> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.removeStudent(id, studentId));
    }
}

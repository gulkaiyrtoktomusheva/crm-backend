package kg.ortcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.ortcrm.dto.attendance.AttendanceMarkRequest;
import kg.ortcrm.dto.attendance.AttendanceResponse;
import kg.ortcrm.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management endpoints")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get attendance for group", description = "Get attendance records for a group on a specific date")
    public ResponseEntity<List<AttendanceResponse>> getByGroupAndDate(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByGroupAndDate(groupId, date));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @Operation(summary = "Get attendance for student", description = "Get all attendance records for a student")
    public ResponseEntity<List<AttendanceResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.findByStudent(studentId));
    }

    @PostMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_MARK')")
    @Operation(summary = "Mark attendance", description = "Bulk mark attendance for a group on a specific date")
    public ResponseEntity<List<AttendanceResponse>> markAttendance(
            @PathVariable Long groupId,
            @Valid @RequestBody AttendanceMarkRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(groupId, request));
    }
}

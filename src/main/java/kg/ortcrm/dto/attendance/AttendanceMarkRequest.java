package kg.ortcrm.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMarkRequest {

    @NotNull(message = "Lesson date is required")
    private LocalDate lessonDate;

    @NotNull(message = "Records are required")
    private List<AttendanceRecord> records;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceRecord {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Present status is required")
        private Boolean present;
    }
}

package kg.ortcrm.dto.lessonattendance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonAttendanceMarkRequest {

    @NotNull(message = "Records are required")
    @Valid
    private List<Record> records;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        @NotNull(message = "Student course ID is required")
        private Long studentCourseId;

        @NotNull(message = "Present status is required")
        private Boolean present;
    }
}

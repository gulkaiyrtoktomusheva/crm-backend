package kg.ortcrm.dto.lesson;

import jakarta.validation.constraints.NotNull;
import kg.ortcrm.entity.enums.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {

    @NotNull(message = "Course subject ID is required")
    private Long courseSubjectId;

    @NotNull(message = "Lesson date is required")
    private LocalDate lessonDate;

    private String topic;
    private LessonStatus status;
}

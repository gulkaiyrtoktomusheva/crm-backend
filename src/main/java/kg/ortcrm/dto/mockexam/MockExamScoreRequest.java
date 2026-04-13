package kg.ortcrm.dto.mockexam;

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
public class MockExamScoreRequest {

    @NotNull(message = "Scores are required")
    private List<ScoreEntry> scores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreEntry {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Subject ID is required")
        private Long subjectId;

        private Integer score;
    }
}

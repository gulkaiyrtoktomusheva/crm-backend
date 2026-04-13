package kg.ortcrm.dto.mockexam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockExamScoreResponse {

    private Long id;
    private Long mockExamId;
    private String mockExamTitle;
    private LocalDate examDate;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Integer score;
    private LocalDateTime createdAt;
}

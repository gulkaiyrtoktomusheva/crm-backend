package kg.ortcrm.dto.mockexam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockExamRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;
}

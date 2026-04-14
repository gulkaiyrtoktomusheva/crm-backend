package kg.ortcrm.dto.studentcourse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kg.ortcrm.entity.enums.StudentCourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @PositiveOrZero(message = "Discount must be zero or positive")
    private BigDecimal discountAmount;
    private StudentCourseStatus status;
}

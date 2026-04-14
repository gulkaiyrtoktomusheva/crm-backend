package kg.ortcrm.dto.studentcourse;

import kg.ortcrm.entity.enums.StudentCourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private BigDecimal coursePrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private StudentCourseStatus status;
    private LocalDateTime createdAt;
}

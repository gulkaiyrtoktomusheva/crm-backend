package kg.ortcrm.dto.lessonattendance;

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
public class LessonAttendanceResponse {

    private Long id;
    private Long lessonId;
    private LocalDate lessonDate;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Long studentCourseId;
    private Long studentId;
    private String studentName;
    private Boolean present;
    private LocalDateTime createdAt;
}

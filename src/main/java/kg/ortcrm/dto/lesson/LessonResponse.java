package kg.ortcrm.dto.lesson;

import kg.ortcrm.entity.enums.LessonStatus;
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
public class LessonResponse {

    private Long id;
    private Long courseSubjectId;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private LocalDate lessonDate;
    private String topic;
    private LessonStatus status;
    private LocalDateTime createdAt;
}

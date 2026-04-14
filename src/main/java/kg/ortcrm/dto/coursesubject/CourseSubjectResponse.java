package kg.ortcrm.dto.coursesubject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSubjectResponse {

    private Long id;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
}

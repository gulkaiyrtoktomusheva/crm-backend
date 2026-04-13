package kg.ortcrm.dto.group;

import kg.ortcrm.dto.student.StudentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {

    private Long id;
    private String name;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private String schedule;
    private String zoomLink;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private List<StudentResponse> students;
}

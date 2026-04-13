package kg.ortcrm.dto.student;

import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String whatsapp;
    private String school;
    private Integer grade;
    private String city;
    private String parentName;
    private String parentPhone;
    private LocalDate ortDate;
    private StudentStatus status;
    private LeadSource source;
    private String referredBy;
    private LocalDateTime createdAt;
    private Set<Long> groupIds;
}

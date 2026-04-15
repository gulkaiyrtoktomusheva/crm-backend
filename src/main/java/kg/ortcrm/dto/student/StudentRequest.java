package kg.ortcrm.dto.student;

import jakarta.validation.constraints.NotBlank;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank(message = "Full name is required")
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
    private Long referredByStudentId;
}

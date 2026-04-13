package kg.ortcrm.dto.lead;

import jakarta.validation.constraints.NotBlank;
import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String whatsapp;
    private String parentName;
    private String parentPhone;
    private LeadSource source;
    private LeadStatus status;
    private String comment;
    private String referredBy;
    private Long assignedToId;
    private LocalDate nextContactDate;
}

package kg.ortcrm.dto.lead;

import kg.ortcrm.entity.enums.LeadSource;
import kg.ortcrm.entity.enums.LeadStatus;
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
public class LeadResponse {

    private Long id;
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
    private String assignedToName;
    private LocalDate nextContactDate;
    private LocalDateTime createdAt;
}

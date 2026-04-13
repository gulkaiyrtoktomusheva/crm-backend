package kg.ortcrm.dto.lead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadStatsResponse {

    private long newCount;
    private long contactedCount;
    private long thinkingCount;
    private long paidCount;
    private long rejectedCount;
    private long totalCount;
}

package sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private long totalUtilisateurs;
    private long totalCoursActifs;
}
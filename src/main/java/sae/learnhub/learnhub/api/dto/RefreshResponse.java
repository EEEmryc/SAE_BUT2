package sae.learnhub.learnhub.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {

    private String token;
    private String type;

    public RefreshResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}

package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;
    private String token;
    private String email;
    private Instant expiryDate;
    private boolean revoked = false;
}

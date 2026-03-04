package sae.learnhub.learnhub.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String statut;
}

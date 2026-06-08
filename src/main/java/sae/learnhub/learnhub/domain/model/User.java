package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String statut;
    private LocalDateTime dateCreation = LocalDateTime.now();
    private String resetToken;
    private LocalDateTime resetTokenExpiration;
    private List<Inscription> inscriptions = new ArrayList<>();
}
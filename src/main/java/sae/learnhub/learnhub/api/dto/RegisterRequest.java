package sae.learnhub.learnhub.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est requis.")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères.")
    private String nom;

    @NotBlank(message = "Le prénom est requis.")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères.")
    private String prenom;

    @NotBlank(message = "L'email est requis.")
    @Email(message = "Le format de l'email est invalide.")
    private String email;

    @NotBlank(message = "Le mot de passe est requis.")
    @Size(min = 6, max = 20, message = "Le mot de passe doit contenir entre 6 et 100 caractères.")
    private String password;

    @NotBlank(message = "Le rôle est requis.")
    private String role;

    private String statut;
}

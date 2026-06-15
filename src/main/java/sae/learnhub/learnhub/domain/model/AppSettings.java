package sae.learnhub.learnhub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings {

    private Long id;

    private Set<String> requestableRoles;

    private boolean inscriptionAutoValidation;

    public static AppSettings defaults() {
        AppSettings settings = new AppSettings();
        settings.setRequestableRoles(Set.of(UserRole.ETUDIANT.name(), UserRole.PROFESSEUR.name()));
        settings.setInscriptionAutoValidation(false);
        return settings;
    }
}

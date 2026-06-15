package sae.learnhub.learnhub.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "parametres_application")
@Data
public class AppSettingsJpaEntity {

    @Id
    private Long id;

    @Column(name = "roles_demandables", nullable = false, length = 255)
    private String requestableRoles;

    @Column(name = "validation_inscription_auto", nullable = false)
    private boolean inscriptionAutoValidation;
}

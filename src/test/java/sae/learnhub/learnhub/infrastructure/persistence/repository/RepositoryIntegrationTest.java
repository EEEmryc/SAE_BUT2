package sae.learnhub.learnhub.infrastructure.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ChapitreJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.CoursJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.InscriptionJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.ProgressionJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.SignalementJpaEntity;
import sae.learnhub.learnhub.infrastructure.persistence.entity.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private SpringDataCoursRepository coursRepository;

    @Autowired
    private SpringDataInscriptionRepository inscriptionRepository;

    @Autowired
    private SpringDataChapitreRepository chapitreRepository;

    @Autowired
    private SpringDataProgressionRepository progressionRepository;

    @Autowired
    private SpringDataSignalementRepository signalementRepository;

    @Test
    void findAllStudents_accepteLesDeuxFormatsDeRole() {
        userRepository.save(user("alice@test.com", "Alice", "Martin", "ETUDIANT"));
        userRepository.save(user("bob@test.com", "Bob", "Durand", "ROLE_ETUDIANT"));
        userRepository.save(user("prof@test.com", "Jean", "Dupont", "PROFESSEUR"));

        List<String> emails = userRepository.findAllStudents().stream()
                .map(UserJpaEntity::getEmail)
                .toList();

        assertEquals(2, emails.size());
        assertTrue(emails.contains("alice@test.com"));
        assertTrue(emails.contains("bob@test.com"));
        assertFalse(emails.contains("prof@test.com"));
    }

    @Test
    void coursRepository_filtreParProfesseurEtStatut() {
        UserJpaEntity prof = userRepository.save(
                user("prof@test.com", "Jean", "Dupont", "PROFESSEUR"));
        UserJpaEntity autreProf = userRepository.save(
                user("autre@test.com", "Marie", "Bernard", "PROFESSEUR"));

        coursRepository.save(cours("Java", "PUBLISHED", prof));
        coursRepository.save(cours("SQL", "DRAFT", prof));
        coursRepository.save(cours("Web", "PUBLISHED", autreProf));

        List<CoursJpaEntity> coursPublies = coursRepository
                .findByProfEmailAndStatut("prof@test.com", "PUBLISHED");

        assertEquals(1, coursPublies.size());
        assertEquals("Java", coursPublies.get(0).getTitre());
        assertEquals(2, coursRepository.countByStatut("PUBLISHED"));
    }

    @Test
    void inscriptionRepository_executeLesJointuresEtLeTri() {
        UserJpaEntity prof = userRepository.save(
                user("prof@test.com", "Jean", "Dupont", "PROFESSEUR"));
        UserJpaEntity eleveZulu = userRepository.save(
                user("zulu@test.com", "Zoe", "Zulu", "ETUDIANT"));
        UserJpaEntity eleveAlpha = userRepository.save(
                user("alpha@test.com", "Alice", "Alpha", "ETUDIANT"));
        CoursJpaEntity cours = coursRepository.save(cours("Java", "PUBLISHED", prof));

        inscriptionRepository.save(inscription(eleveZulu, cours, "VALIDE"));
        inscriptionRepository.save(inscription(eleveAlpha, cours, "VALIDE"));

        List<CoursJpaEntity> coursEleve = inscriptionRepository
                .findCoursByEleveEmail("alpha@test.com");
        List<InscriptionJpaEntity> inscriptionsProf = inscriptionRepository
                .findByCoursProf("prof@test.com");

        assertEquals(List.of(cours.getId()), coursEleve.stream().map(CoursJpaEntity::getId).toList());
        assertEquals(List.of("Alpha", "Zulu"),
                inscriptionsProf.stream().map(i -> i.getEleve().getNom()).toList());
        assertTrue(inscriptionRepository.existsByEleveEmailAndCoursId(
                "alpha@test.com", cours.getId()));
    }

    @Test
    void progressionRepository_rechercheEtCompteParRelations() {
        UserJpaEntity prof = userRepository.save(
                user("prof@test.com", "Jean", "Dupont", "PROFESSEUR"));
        UserJpaEntity eleve = userRepository.save(
                user("eleve@test.com", "Sophie", "Martin", "ETUDIANT"));
        CoursJpaEntity cours = coursRepository.save(cours("Java", "PUBLISHED", prof));
        ChapitreJpaEntity chapitreDeux = chapitreRepository.save(chapitre("Classes", 2, cours));
        ChapitreJpaEntity chapitreUn = chapitreRepository.save(chapitre("Variables", 1, cours));

        progressionRepository.save(progression(eleve, cours, chapitreUn, "TERMINE", 100));
        progressionRepository.save(progression(eleve, cours, chapitreDeux, "EN_COURS", 50));

        assertEquals(1, progressionRepository.countByEleveEmailAndCoursIdAndStatut(
                "eleve@test.com", cours.getId(), "TERMINE"));
        assertTrue(progressionRepository.findByEleveEmailAndChapitreId(
                "eleve@test.com", chapitreUn.getId()).isPresent());
        assertEquals(List.of(1, 2), chapitreRepository.findByCoursIdOrderByOrdreAsc(cours.getId())
                .stream().map(ChapitreJpaEntity::getOrdre).toList());
    }

    @Test
    void signalementRepository_trieDuPlusRecentAuPlusAncien() {
        UserJpaEntity auteur = userRepository.save(
                user("eleve@test.com", "Sophie", "Martin", "ETUDIANT"));

        signalementRepository.save(signalement(
                "Ancien problème",
                LocalDateTime.of(2026, 6, 10, 9, 0),
                auteur));
        signalementRepository.save(signalement(
                "Problème récent",
                LocalDateTime.of(2026, 6, 13, 14, 30),
                auteur));

        assertEquals(
                List.of("Problème récent", "Ancien problème"),
                signalementRepository.findAllByOrderByDateEnvoiDesc()
                        .stream()
                        .map(SignalementJpaEntity::getSujet)
                        .toList());
    }

    private UserJpaEntity user(String email, String prenom, String nom, String role) {
        UserJpaEntity user = new UserJpaEntity();
        user.setEmail(email);
        user.setPrenom(prenom);
        user.setNom(nom);
        user.setPassword("password");
        user.setRole(role);
        user.setStatut("ACTIF");
        return user;
    }

    private CoursJpaEntity cours(String titre, String statut, UserJpaEntity prof) {
        CoursJpaEntity cours = new CoursJpaEntity();
        cours.setTitre(titre);
        cours.setDescription("Description " + titre);
        cours.setStatut(statut);
        cours.setVisibleCatalogue(true);
        cours.setProf(prof);
        return cours;
    }

    private InscriptionJpaEntity inscription(
            UserJpaEntity eleve,
            CoursJpaEntity cours,
            String statut) {
        InscriptionJpaEntity inscription = new InscriptionJpaEntity();
        inscription.setEleve(eleve);
        inscription.setCours(cours);
        inscription.setStatut(statut);
        return inscription;
    }

    private ChapitreJpaEntity chapitre(String titre, int ordre, CoursJpaEntity cours) {
        ChapitreJpaEntity chapitre = new ChapitreJpaEntity();
        chapitre.setTitre(titre);
        chapitre.setContenu("Contenu " + titre);
        chapitre.setOrdre(ordre);
        chapitre.setCours(cours);
        return chapitre;
    }

    private ProgressionJpaEntity progression(
            UserJpaEntity eleve,
            CoursJpaEntity cours,
            ChapitreJpaEntity chapitre,
            String statut,
            int pourcentage) {
        ProgressionJpaEntity progression = new ProgressionJpaEntity();
        progression.setEleve(eleve);
        progression.setCours(cours);
        progression.setChapitre(chapitre);
        progression.setStatut(statut);
        progression.setPourcentage(pourcentage);
        return progression;
    }

    private SignalementJpaEntity signalement(
            String sujet,
            LocalDateTime dateEnvoi,
            UserJpaEntity auteur) {
        SignalementJpaEntity signalement = new SignalementJpaEntity();
        signalement.setSujet(sujet);
        signalement.setDescription("Description du signalement");
        signalement.setCategorie("TECHNIQUE");
        signalement.setStatut("NOUVEAU");
        signalement.setDateEnvoi(dateEnvoi);
        signalement.setAuteur(auteur);
        return signalement;
    }
}

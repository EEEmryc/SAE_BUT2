package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.model.Cours;
import sae.learnhub.learnhub.domain.model.Inscription;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.CoursRepository;
import sae.learnhub.learnhub.domain.repository.InscriptionRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InscriptionAccessTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CoursRepository coursRepository;
    @Autowired private InscriptionRepository inscriptionRepository;

    private Long inscriptionId;

    @BeforeEach
    void setup() {
        inscriptionRepository.deleteAll();
        coursRepository.deleteAll();
        userRepository.deleteAll();

        // Professeur responsable
        User prof = new User();
        prof.setEmail("prof.responsable@test.com");
        prof.setRole("PROFESSEUR");
        prof.setNom("Nom"); prof.setPrenom("Prenom"); prof.setPassword("pass");
        userRepository.save(prof);

        // Élève
        User eleve = new User();
        eleve.setEmail("eleve@test.com");
        eleve.setRole("ELEVE");
        eleve.setNom("Nom"); eleve.setPrenom("Prenom"); eleve.setPassword("pass");
        userRepository.save(eleve);

        // Cours lié au prof
        Cours cours = new Cours();
        cours.setTitre("Cours Securise");
        cours.setProf(prof);
        cours = coursRepository.save(cours);

        // Inscription à tester
        Inscription ins = new Inscription();
        ins.setCours(cours);
        ins.setEleve(eleve);
        ins.setStatut("EN_ATTENTE");
        inscriptionId = inscriptionRepository.save(ins).getId();
    }

    @Test
    @WithMockUser(username = "prof.responsable@test.com", roles = "PROFESSEUR")
    void profResponsableShouldUpdateStatut() throws Exception {
        mockMvc.perform(patch("/api/inscriptions/" + inscriptionId + "/statut")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\": \"VALIDE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "autre.prof@test.com", roles = "PROFESSEUR")
    void autreProfShouldBeForbidden() throws Exception {
        mockMvc.perform(patch("/api/inscriptions/" + inscriptionId + "/statut")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\": \"VALIDE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATEUR")
    void adminShouldAlwaysUpdateStatut() throws Exception {
        mockMvc.perform(patch("/api/inscriptions/" + inscriptionId + "/statut")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\": \"VALIDE\"}"))
                .andExpect(status().isOk());
    }
}
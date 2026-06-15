package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.repository.IAppSettingsRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAppSettingsRepository appSettingsRepository;

    @BeforeEach
    void resetSettings() {
        appSettingsRepository.save(sae.learnhub.learnhub.domain.model.AppSettings.defaults());
    }

    @Test
    void getSettings_renvoieLesValeursParDefaut() throws Exception {
        mockMvc.perform(get("/api/admin/settings")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestableRoles", org.hamcrest.Matchers.containsInAnyOrder("ETUDIANT", "PROFESSEUR")))
                .andExpect(jsonPath("$.inscriptionAutoValidation").value(false));
    }

    @Test
    void updateSettings_avecValeursValides_metAJourLesParametres() throws Exception {
        mockMvc.perform(put("/api/admin/settings")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "requestableRoles": ["ETUDIANT"],
                                  "inscriptionAutoValidation": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestableRoles", org.hamcrest.Matchers.contains("ETUDIANT")))
                .andExpect(jsonPath("$.inscriptionAutoValidation").value(true));
    }

    @Test
    void updateSettings_avecRolesVides_renvoieErreurMetier() throws Exception {
        mockMvc.perform(put("/api/admin/settings")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "requestableRoles": []
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSettings_sansRoleAdmin_estRefuse() throws Exception {
        mockMvc.perform(get("/api/admin/settings")
                        .with(user("etudiant").roles("ETUDIANT")))
                .andExpect(status().isForbidden());
    }
}

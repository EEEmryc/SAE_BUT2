package sae.learnhub.learnhub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.RefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndLogin() throws Exception {
        String registerBody = """
            {
              "nom": "Zakaria",
              "prenom": "Test",
              "email": "zakaria@test.com",
              "password": "pass123",
              "role": "ELEVE",
              "statut": "Actif"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("zakaria@test.com"));

        String loginBody = """
            {
              "email": "zakaria@test.com",
              "password": "pass123"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void testLogout() throws Exception {
        // Préparation d'un utilisateur et d'un token simulé
        String registerBody = "{\"nom\":\"L\",\"prenom\":\"T\",\"email\":\"out@t.com\",\"password\":\"p\",\"role\":\"ELEVE\"}";
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"out@t.com\",\"password\":\"p\"}"))
                .andReturn().getResponse().getContentAsString();

        String refreshToken = new org.json.JSONObject(loginResponse).getString("refreshToken");
        String accessToken = new org.json.JSONObject(loginResponse).getString("token");

        mockMvc.perform(post("/api/auth/logout")
                        .header("X-Refresh-Token", refreshToken)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Déconnexion réussie"));
    }

    @Test
    void testForgotPassword() throws Exception {
        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail("forgot@test.com");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setRole("ELEVE");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"forgot@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Jeton de réinitialisation généré"));
    }

    @Test
    void testResetPassword() throws Exception {
        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail("reset@test.com");
        user.setPassword(passwordEncoder.encode("oldpass"));
        user.setRole("ELEVE");
        user.setResetToken("valid-token");
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetBody = """
            {
              "token": "valid-token",
              "newPassword": "newPassword123"
            }
        """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe réinitialisé avec succès"));
    }
}

package sae.learnhub.learnhub.api.controller;

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
import sae.learnhub.learnhub.domain.repository.IRefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private IRefreshTokenRepository refreshTokenRepository;

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
                                      "role": "ETUDIANT",
                                      "statut": "ACTIF"
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
                String registerBody = "{\"nom\":\"Lu\",\"prenom\":\"To\",\"email\":\"out@t.com\",\"password\":\"pass123\",\"role\":\"ETUDIANT\"}";
                mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody));

                String loginResponse = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"out@t.com\",\"password\":\"pass123\"}"))
                                .andReturn().getResponse().getContentAsString();

                String refreshToken = new org.json.JSONObject(loginResponse).getString("refreshToken");
                String accessToken = new org.json.JSONObject(loginResponse).getString("token");

                mockMvc.perform(post("/api/auth/logout")
                                .header("X-Refresh-Token", refreshToken)
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Déconnexion réussie"));
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
                                .andExpect(jsonPath("$.message").value("Jeton de réinitialisation généré"))
                                .andExpect(jsonPath("$.token").exists());
        }

        @Test
        void testResetPasswordWithReturnedToken() throws Exception {
                User user = new User();
                user.setNom("Test");
                user.setPrenom("User");
                user.setEmail("reset2@test.com");
                user.setPassword(passwordEncoder.encode("oldpass"));
                user.setRole("ELEVE");
                userRepository.save(user);

                String forgotResponse = mockMvc.perform(post("/api/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"reset2@test.com\"}"))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                String token = new org.json.JSONObject(forgotResponse).getString("token");
                String resetBody = String.format("{\"token\":\"%s\",\"newPassword\":\"newPassword123\"}", token);

                mockMvc.perform(post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(resetBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Mot de passe réinitialisé avec succès"));
        }
}
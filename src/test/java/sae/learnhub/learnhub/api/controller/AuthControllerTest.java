package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sae.learnhub.learnhub.domain.repository.RefreshTokenRepository;
import sae.learnhub.learnhub.domain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void cleanDb() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerTest() throws Exception {
        String body = """
            {
              "nom": "Elyess",
              "prenom": "Test",
              "email": "elyess@test.com",
              "password": "pass123",
              "role": "ELEVE"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("elyess@test.com"));
    }

    @Test
    void testRegisterLogin() throws Exception {

        String registerBody = """
            {
              "nom": "Zakaria",
              "prenom": "Test",
              "email": "zakaria@test.com",
              "password": "pass123",
              "role": "ELEVE"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

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
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void testRefreshToken() throws Exception {
        // Register and login first
        String registerBody = """
            {
              "nom": "Refresh",
              "prenom": "Test",
              "email": "refresh@test.com",
              "password": "pass123",
              "role": "ELEVE"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = """
            {
              "email": "refresh@test.com",
              "password": "pass123"
            }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract refresh token from response
        String refreshToken = new org.json.JSONObject(response).getString("refreshToken");

        // Test refresh endpoint
        mockMvc.perform(post("/api/auth/refresh")
                        .header("X-Refresh-Token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void testRefreshTokenInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .header("X-Refresh-Token", "invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogout() throws Exception {
        // Register and login first
        String registerBody = """
            {
              "nom": "Logout",
              "prenom": "Test",
              "email": "logout@test.com",
              "password": "pass123",
              "role": "ELEVE"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = """
            {
              "email": "logout@test.com",
              "password": "pass123"
            }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = new org.json.JSONObject(response).getString("refreshToken");

        // Test logout
        mockMvc.perform(post("/api/auth/logout")
                        .header("X-Refresh-Token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        // After logout, refresh token should be revoked
        mockMvc.perform(post("/api/auth/refresh")
                        .header("X-Refresh-Token", refreshToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterExist() throws Exception {
        String body = """
            {
              "nom": "Elx",
              "prenom": "Test",
              "email": "elx@test.com",
              "password": "pass123",
              "role": "ELEVE"
            }
        """;
        
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email deja exist"));
    }

    @Test
    void testLoginInvalid() throws Exception {
        String body = """
            {
              "email": "faux@test.com",
              "password": "faux"
            }
        """;
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
      }
    
}
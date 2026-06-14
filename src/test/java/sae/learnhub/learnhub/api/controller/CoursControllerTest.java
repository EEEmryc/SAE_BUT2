package sae.learnhub.learnhub.api.controller;

import org.junit.jupiter.api.BeforeEach;
import sae.learnhub.learnhub.domain.repository.IUserRepository;
import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.model.Cours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import sae.learnhub.learnhub.domain.repository.ICoursRepository;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CoursControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    ICoursRepository coursRepository;
    @Autowired
    AuthService authService;

    private Long coursId;

    @BeforeEach
    void setup() {
        coursRepository.deleteAll();
        userRepository.deleteAll();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User prof1 = new User();
        prof1.setNom("Prof");
        prof1.setPrenom("One");
        prof1.setEmail("prof1@test.com");
        prof1.setPassword(passwordEncoder.encode("password123"));
        prof1.setRole("PROFESSEUR");
        prof1 = userRepository.save(prof1);

        User prof2 = new User();
        prof2.setNom("Prof");
        prof2.setPrenom("Two");
        prof2.setEmail("prof2@test.com");
        prof2.setPassword(passwordEncoder.encode("password123"));
        prof2.setRole("PROFESSEUR");
        userRepository.save(prof2);

        User admin = new User();
        admin.setNom("Admin");
        admin.setPrenom("User");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        Cours cours = new Cours();
        cours.onCreate(); // Remplace les "set" manuels de base (DRAFT, etc.)
        cours.setTitre("Java");
        cours.setDescription("Base");
        cours.setProf(prof1);

        coursId = coursRepository.save(cours).getId();
    }

    @Test
    void getAllCours() throws Exception {
        mockMvc.perform(get("/api/cours"))
                .andExpect(status().isOk());
    }

    @Test
    void profPeutConsulterLeDetailDeSonCours() throws Exception {
        String jwtToken = getJwtToken("prof1@test.com", "password123");

        mockMvc.perform(get("/api/cours/" + coursId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Java"));
    }

    @Test
    void autreProfNePeutPasConsulterLeDetailDuCours() throws Exception {
        String jwtToken = getJwtToken("prof2@test.com", "password123");

        mockMvc.perform(get("/api/cours/" + coursId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void profPeutConsulterLeResumeDeSonCours() throws Exception {
        String jwtToken = getJwtToken("prof1@test.com", "password123");

        mockMvc.perform(get("/api/cours/" + coursId + "/summary")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students").value(0))
                .andExpect(jsonPath("$.chapters").value(0))
                .andExpect(jsonPath("$.resources").value(0))
                .andExpect(jsonPath("$.averageProgress").value(0));
    }

    @Test
    void profPeutAjouterUnFichierPrincipalAuCours() throws Exception {
        String jwtToken = getJwtToken("prof1@test.com", "password123");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cours-java.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "support principal".getBytes());

        mockMvc.perform(multipart("/api/cours/" + coursId + "/fichier-principal")
                .file(file)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fichierPrincipalNom").value("cours-java.pdf"))
                .andExpect(jsonPath("$.fichierPrincipalType").value("PDF"))
                .andExpect(jsonPath("$.fichierPrincipalTailleOctets").value(17))
                .andExpect(jsonPath("$.fichierPrincipalUrl").value(
                        org.hamcrest.Matchers.startsWith("/api/files/resources/")));
    }

    @Test
    void profUpdateCours() throws Exception {
        String jwtToken = getJwtToken("prof1@test.com", "password123");
        String body = """
                    {
                      "titre": "javaScript",
                      "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
                      "statut": "PUBLISHED",
                      "visibleCatalogue": true
                    }
                """;

        mockMvc.perform(put("/api/cours/" + coursId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void autreProfUpdateCours() throws Exception {
        String jwtToken = getJwtToken("prof2@test.com", "password123");
        String body = """
                    {
                      "titre": "Mathematique",
                      "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
                      "statut": "PUBLISHED",
                      "visibleCatalogue": true
                    }
                """;

        mockMvc.perform(put("/api/cours/" + coursId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUpdateCours() throws Exception {
        String jwtToken = getJwtToken("admin@test.com", "password123");
        String body = """
                    {
                      "titre": "Admin test",
                      "description": "lorem ipsum dolor sit amet consectetur adipiscing elit",
                      "statut": "PUBLISHED",
                      "visibleCatalogue": true
                    }
                """;

        mockMvc.perform(put("/api/cours/" + coursId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .content(body))
                .andExpect(status().isForbidden());
    }

    private String getJwtToken(String email, String password) {
        return authService.login(new AuthService.LoginCommand(email, password)).token();
    }
}

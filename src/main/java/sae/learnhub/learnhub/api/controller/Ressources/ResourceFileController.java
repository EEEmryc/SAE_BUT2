package sae.learnhub.learnhub.api.controller.Ressources;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.application.port.ResourceFileStorage;
import sae.learnhub.learnhub.application.Ressource_Service.ResourceFileAccessService;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files/resources")
@RequiredArgsConstructor
public class ResourceFileController {

    private final ResourceFileStorage fileStorage;
    private final ResourceFileAccessService fileAccessService;

    @GetMapping("/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable String key,
            Authentication authentication) {
        fileAccessService.verifyAccess(
                key,
                authentication.getName(),
                hasAuthority(authentication, "ROLE_PROFESSEUR"),
                hasAuthority(authentication, "ROLE_ETUDIANT"),
                hasAuthority(authentication, "ROLE_ADMIN"));
        ResourceFileStorage.FileContent file = fileStorage.load(key);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.originalName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new InputStreamResource(file.content()));
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(candidate -> candidate.getAuthority().equals(authority));
    }
}

package sae.learnhub.learnhub.api.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.learnhub.learnhub.api.dto.settings.AppSettingsResponse;
import sae.learnhub.learnhub.api.dto.settings.AppSettingsUpdateRequest;
import sae.learnhub.learnhub.api.mapper.AppSettingsMapper;
import sae.learnhub.learnhub.application.settings.AppSettingsService;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AppSettingsController {

    private final AppSettingsService service;
    private final AppSettingsMapper mapper;

    @GetMapping
    public ResponseEntity<AppSettingsResponse> getSettings() {
        return ResponseEntity.ok(mapper.toResponse(service.getSettings()));
    }

    @PutMapping
    public ResponseEntity<AppSettingsResponse> updateSettings(
            @RequestBody AppSettingsUpdateRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.updateSettings(mapper.toCommand(request))));
    }
}

package sae.learnhub.learnhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    @GetMapping("/auth")
    public Map<String, Object> getAuthInfo(Authentication authentication) {
        Map<String, Object> authInfo = new HashMap<>();

        if (authentication != null) {
            authInfo.put("authenticated", true);
            authInfo.put("username", authentication.getName());
            authInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            authInfo.put("principal", authentication.getPrincipal().getClass().getSimpleName());
        } else {
            authInfo.put("authenticated", false);
            authInfo.put("message", "No authentication found");
        }

        return authInfo;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Debug endpoint working");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
}
package sae.elearning.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import sae.elearning.api.dto.StatsResponse;
import sae.elearning.api.dto.UserCreateRequest;
import sae.elearning.api.dto.UserResponse;
import sae.elearning.api.dto.UserUpdateRequest;
import sae.elearning.api.mapper.AdminMapper;
import sae.elearning.application.service.AdminService;
import sae.elearning.application.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final AdminMapper adminMapper;

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(adminMapper.toResponse(adminService.getGlobalStatistics()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers().stream()
                        .map(adminMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(
                adminMapper.toResponse(userService.createUser(adminMapper.toCommand(request)))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(
                adminMapper.toResponse(userService.updateUser(id, adminMapper.toCommand(request)))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
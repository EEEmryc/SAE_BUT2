package sae.learnhub.learnhub.api.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import sae.learnhub.learnhub.api.dto.admin.StatsResponse;
import sae.learnhub.learnhub.api.dto.user.UserCreateRequest;
import sae.learnhub.learnhub.api.dto.user.UserCreationResponse;
import sae.learnhub.learnhub.api.dto.user.UserResponse;
import sae.learnhub.learnhub.api.dto.user.UserUpdateRequest;
import sae.learnhub.learnhub.api.mapper.AdminMapper;
import sae.learnhub.learnhub.application.admin.AdminService;
import sae.learnhub.learnhub.application.user.UserService;

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
    public ResponseEntity<UserCreationResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
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

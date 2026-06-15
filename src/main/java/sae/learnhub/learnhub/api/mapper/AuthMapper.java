package sae.learnhub.learnhub.api.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.api.dto.auth.AuthResponse;
import sae.learnhub.learnhub.api.dto.auth.LoginRequest;
import sae.learnhub.learnhub.api.dto.auth.ResetPasswordRequest;
import sae.learnhub.learnhub.api.dto.auth.RegisterRequest;
import sae.learnhub.learnhub.api.dto.auth.RefreshResponse;
import sae.learnhub.learnhub.api.dto.auth.UpdateProfileRequest;
import sae.learnhub.learnhub.api.dto.user.UserResponse;
import sae.learnhub.learnhub.application.auth.AuthService;

@Component
public class AuthMapper {

    public AuthService.RegisterCommand toCommand(RegisterRequest request) {
        return new AuthService.RegisterCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut());
    }

    public UserResponse toResponse(AuthService.UserResult result) {
        return new UserResponse(
                result.id(),
                result.nom(),
                result.prenom(),
                result.email(),
                result.role(),
                result.statut(),
                result.dateCreation());
    }

    public AuthService.LoginCommand toCommand(LoginRequest request) {
        return new AuthService.LoginCommand(request.email(), request.password());
    }

    public AuthResponse toResponse(AuthService.AuthResult result) {
        return new AuthResponse(result.token(), result.refreshToken());
    }

    public RefreshResponse toResponse(AuthService.RefreshResult result) {
        return new RefreshResponse(result.token());
    }

    public AuthService.ResetPasswordCommand toCommand(ResetPasswordRequest request) {
        return new AuthService.ResetPasswordCommand(request.token(), request.newPassword());
    }

    public AuthService.UpdateProfileCommand toCommand(UpdateProfileRequest request) {
        return new AuthService.UpdateProfileCommand(request.nom(), request.prenom(), request.password());
    }
}

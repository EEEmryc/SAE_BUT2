package sae.learnhub.learnhub.api.mapper;

import org.springframework.stereotype.Component;

import sae.learnhub.learnhub.api.dto.Auth_DTO.AuthResponse;
import sae.learnhub.learnhub.api.dto.Auth_DTO.LoginRequest;
import sae.learnhub.learnhub.api.dto.Auth_DTO.ResetPasswordRequest;
import sae.learnhub.learnhub.api.dto.Register.RegisterRequest;
import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.RefreshResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.application.Auth_Service.AuthService;

@Component
public class AuthMapper {

    public AuthService.RegisterCommand toCommand(RegisterRequest request) {
        return new AuthService.RegisterCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut()
        );
    }

    public UserResponse toResponse(AuthService.UserResult result) {
        return new UserResponse(
                result.id(),
                result.nom(),
                result.prenom(),
                result.email(),
                result.role(),
                result.statut()
        );
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
}

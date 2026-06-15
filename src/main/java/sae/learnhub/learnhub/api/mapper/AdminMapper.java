package sae.learnhub.learnhub.api.mapper;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.api.dto.admin.StatsResponse;
import sae.learnhub.learnhub.api.dto.user.UserCreateRequest;
import sae.learnhub.learnhub.api.dto.user.UserCreationResponse;
import sae.learnhub.learnhub.api.dto.user.UserResponse;
import sae.learnhub.learnhub.api.dto.user.UserUpdateRequest;
import sae.learnhub.learnhub.application.admin.AdminService;
import sae.learnhub.learnhub.application.user.UserService;

@Component
public class AdminMapper {

    public StatsResponse toResponse(AdminService.GlobalStatistics stats) {
        return new StatsResponse(stats.totalUsers(), stats.activeCourses());
    }

    public UserResponse toResponse(UserService.UserResult result) {
        return new UserResponse(
                result.id(),
                result.nom(),
                result.prenom(),
                result.email(),
                result.role(),
                result.statut(),
                result.dateCreation());
    }

    public UserCreationResponse toResponse(UserService.UserCreationResult result) {
        return new UserCreationResponse(
                toResponse(result.user()),
                result.invitationEmailSent());
    }

    public UserService.UserCommand toCommand(UserCreateRequest request) {
        return new UserService.UserCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut());
    }

    public UserService.UserCommand toCommand(UserUpdateRequest request) {
        return new UserService.UserCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut());
    }
}

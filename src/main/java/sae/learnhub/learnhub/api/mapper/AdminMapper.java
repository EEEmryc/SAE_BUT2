package sae.elearning.api.mapper;

import org.springframework.stereotype.Component;
import sae.elearning.api.dto.StatsResponse;
import sae.elearning.api.dto.UserCreateRequest;
import sae.elearning.api.dto.UserResponse;
import sae.elearning.api.dto.UserUpdateRequest;
import sae.elearning.application.service.AdminService;
import sae.elearning.application.service.UserService;

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
                result.statut()
        );
    }

    public UserService.UserCommand toCommand(UserCreateRequest request) {
        return new UserService.UserCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut()
        );
    }

    public UserService.UserCommand toCommand(UserUpdateRequest request) {
        return new UserService.UserCommand(
                request.nom(),
                request.prenom(),
                request.email(),
                request.password(),
                request.role(),
                request.statut()
        );
    }
}
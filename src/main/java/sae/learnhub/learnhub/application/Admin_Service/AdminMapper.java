package sae.learnhub.learnhub.application.Admin_Service;

import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.api.dto.Stat_Refresh_DTO.StatsResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserCreateRequest;
import sae.learnhub.learnhub.api.dto.User_DTO.UserResponse;
import sae.learnhub.learnhub.api.dto.User_DTO.UserUpdateRequest;
import sae.learnhub.learnhub.application.User_Service.UserService;

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
                result.statut());
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
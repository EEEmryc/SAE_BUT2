package sae.learnhub.learnhub.api.dto.User_DTO;

public record UserCreationResponse(
        UserResponse user,
        boolean invitationEmailSent
) {
}

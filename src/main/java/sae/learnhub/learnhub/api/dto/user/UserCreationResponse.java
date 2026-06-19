package sae.learnhub.learnhub.api.dto.user;

public record UserCreationResponse(
        UserResponse user,
        boolean invitationEmailSent
) {
}

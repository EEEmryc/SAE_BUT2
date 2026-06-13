package sae.learnhub.learnhub.application.port;

public interface AccountNotificationSender {

    boolean sendAccountInvitation(String email, String firstName, String resetToken);

    boolean sendPasswordReset(String email, String firstName, String resetToken);
}

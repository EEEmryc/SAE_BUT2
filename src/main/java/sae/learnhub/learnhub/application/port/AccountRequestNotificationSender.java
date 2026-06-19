package sae.learnhub.learnhub.application.port;

public interface AccountRequestNotificationSender {

    boolean sendRequestReceived(String email, String firstName);

    boolean sendRequestRejected(String email, String firstName);
}

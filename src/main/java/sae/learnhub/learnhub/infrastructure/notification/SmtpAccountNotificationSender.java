package sae.learnhub.learnhub.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.application.port.AccountNotificationSender;
import sae.learnhub.learnhub.application.port.AccountRequestNotificationSender;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpAccountNotificationSender
        implements AccountNotificationSender, AccountRequestNotificationSender {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from:no-reply@learnhub.local}")
    private String from;

    @Value("${app.frontend.reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    @Override
    public boolean sendAccountInvitation(String email, String firstName, String resetToken) {
        String body = """
                Bonjour %s,

                Un compte LearnHub vient d'être créé pour vous.
                Pour choisir votre mot de passe, ouvrez ce lien valable pendant une heure :

                %s

                Si vous n'êtes pas à l'origine de cette demande, contactez un administrateur.
                """.formatted(firstName, buildResetLink(resetToken));

        return send(email, "Bienvenue sur LearnHub", body);
    }

    @Override
    public boolean sendPasswordReset(String email, String firstName, String resetToken) {
        String body = """
                Bonjour %s,

                Une réinitialisation de votre mot de passe LearnHub a été demandée.
                Ouvrez ce lien valable pendant une heure :

                %s

                Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.
                """.formatted(firstName, buildResetLink(resetToken));

        return send(email, "Réinitialisation de votre mot de passe LearnHub", body);
    }

    @Override
    public boolean sendRequestReceived(String email, String firstName) {
        String body = """
                Bonjour %s,

                Votre demande de création de compte LearnHub a bien été reçue.
                Un administrateur va l'examiner prochainement.

                Vous recevrez un nouvel email dès qu'une décision sera prise.
                """.formatted(firstName);

        return send(email, "Demande de compte LearnHub reçue", body);
    }

    @Override
    public boolean sendRequestRejected(String email, String firstName) {
        String body = """
                Bonjour %s,

                Après examen, votre demande de création de compte LearnHub
                n'a pas été acceptée.

                Vous pouvez contacter l'administration pour obtenir plus d'informations.
                """.formatted(firstName);

        return send(email, "Décision concernant votre demande LearnHub", body);
    }

    private boolean send(String recipient, String subject, String body) {
        if (!enabled) {
            log.info("Envoi d'email désactivé pour {}", recipient);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            log.warn("Impossible d'envoyer l'email LearnHub à {}", recipient, exception);
            return false;
        }
    }

    private String buildResetLink(String resetToken) {
        String encodedToken = URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        return resetPasswordUrl + "?token=" + encodedToken;
    }
}

package sae.learnhub.learnhub.infrastructure.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import sae.learnhub.learnhub.application.port.AccountNotificationSender;
import sae.learnhub.learnhub.application.port.AccountRequestNotificationSender;
import sae.learnhub.learnhub.infrastructure.config.MailDeliveryProperties;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpAccountNotificationSender
        implements AccountNotificationSender, AccountRequestNotificationSender {

    private final JavaMailSender mailSender;
    private final MailDeliveryProperties properties;

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

        return send(
                email,
                "Bienvenue sur LearnHub",
                body,
                invitationHtml(firstName, buildResetLink(resetToken)));
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

        return send(
                email,
                "Réinitialisation de votre mot de passe LearnHub",
                body,
                resetPasswordHtml(firstName, buildResetLink(resetToken)));
    }

    @Override
    public boolean sendRequestReceived(String email, String firstName) {
        String body = """
                Bonjour %s,

                Votre demande de création de compte LearnHub a bien été reçue.
                Un administrateur va l'examiner prochainement.

                Vous recevrez un nouvel email dès qu'une décision sera prise.
                """.formatted(firstName);

        return send(
                email,
                "Demande de compte LearnHub reçue",
                body,
                requestReceivedHtml(firstName));
    }

    @Override
    public boolean sendRequestRejected(String email, String firstName) {
        String body = """
                Bonjour %s,

                Après examen, votre demande de création de compte LearnHub
                n'a pas été acceptée.

                Vous pouvez contacter l'administration pour obtenir plus d'informations.
                """.formatted(firstName);

        return send(
                email,
                "Décision concernant votre demande LearnHub",
                body,
                requestRejectedHtml(firstName));
    }

    private boolean send(
            String recipient,
            String subject,
            String textBody,
            String htmlBody
    ) {
        if (!properties.isEnabled()) {
            log.info("Envoi d'email désactivé pour {}", recipient);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(properties.getFrom(), properties.getFromName());
            if (properties.getReplyTo() != null
                    && !properties.getReplyTo().isBlank()) {
                helper.setReplyTo(properties.getReplyTo());
            }
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(textBody, htmlBody);
            mailSender.send(message);
            log.info("Email LearnHub envoyé à {}", recipient);
            return true;
        } catch (MailException | MessagingException | UnsupportedEncodingException exception) {
            log.warn("Impossible d'envoyer l'email LearnHub à {}", recipient, exception);
            return false;
        }
    }

    private String buildResetLink(String resetToken) {
        String encodedToken = URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        return resetPasswordUrl + "?token=" + encodedToken;
    }

    private String invitationHtml(String firstName, String resetLink) {
        return emailLayout(
                "Bienvenue sur LearnHub",
                """
                <p>Bonjour %s,</p>
                <p>Un compte LearnHub vient d'être créé pour vous.</p>
                <p>Choisissez votre mot de passe à l'aide du bouton ci-dessous.
                Ce lien est valable pendant une heure.</p>
                %s
                <p class="muted">Si vous n'êtes pas à l'origine de cette demande,
                contactez un administrateur.</p>
                """.formatted(
                        safe(firstName),
                        actionButton("Choisir mon mot de passe", resetLink)));
    }

    private String resetPasswordHtml(String firstName, String resetLink) {
        return emailLayout(
                "Réinitialisation du mot de passe",
                """
                <p>Bonjour %s,</p>
                <p>Une réinitialisation de votre mot de passe LearnHub a été demandée.</p>
                <p>Ce lien est valable pendant une heure.</p>
                %s
                <p class="muted">Si vous n'êtes pas à l'origine de cette demande,
                ignorez cet e-mail.</p>
                """.formatted(
                        safe(firstName),
                        actionButton("Réinitialiser mon mot de passe", resetLink)));
    }

    private String requestReceivedHtml(String firstName) {
        return emailLayout(
                "Demande reçue",
                """
                <p>Bonjour %s,</p>
                <p>Votre demande de création de compte LearnHub a bien été reçue.</p>
                <p>Un administrateur va l'examiner prochainement. Vous recevrez un
                nouvel e-mail dès qu'une décision sera prise.</p>
                """.formatted(safe(firstName)));
    }

    private String requestRejectedHtml(String firstName) {
        return emailLayout(
                "Décision concernant votre demande",
                """
                <p>Bonjour %s,</p>
                <p>Après examen, votre demande de création de compte LearnHub
                n'a pas été acceptée.</p>
                <p>Vous pouvez contacter l'administration pour obtenir plus
                d'informations.</p>
                """.formatted(safe(firstName)));
    }

    private String actionButton(String label, String url) {
        return """
                <p style="margin:28px 0">
                  <a href="%s" style="display:inline-block;padding:13px 22px;
                  border-radius:10px;background:#5364f4;color:#fff;
                  text-decoration:none;font-weight:700">%s</a>
                </p>
                """.formatted(safe(url), safe(label));
    }

    private String emailLayout(String title, String content) {
        return """
                <!doctype html>
                <html lang="fr">
                  <body style="margin:0;background:#f4f5ff;font-family:Arial,sans-serif;
                  color:#111936">
                    <div style="max-width:640px;margin:0 auto;padding:32px 16px">
                      <div style="background:#fff;border:1px solid #e3e6f5;
                      border-radius:18px;overflow:hidden">
                        <div style="padding:22px 28px;background:linear-gradient(
                        120deg,#4f5ff7,#8056ec);color:#fff;font-size:22px;
                        font-weight:800">LearnHub</div>
                        <div style="padding:30px 28px;line-height:1.65">
                          <h1 style="font-size:24px;margin:0 0 20px">%s</h1>
                          %s
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(safe(title), content);
    }

    private String safe(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }
}

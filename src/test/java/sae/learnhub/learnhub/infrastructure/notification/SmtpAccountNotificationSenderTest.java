package sae.learnhub.learnhub.infrastructure.notification;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import sae.learnhub.learnhub.infrastructure.config.MailDeliveryProperties;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmtpAccountNotificationSenderTest {

    @Mock
    private JavaMailSender mailSender;

    private MailDeliveryProperties properties;
    private SmtpAccountNotificationSender sender;

    @BeforeEach
    void setUp() {
        properties = new MailDeliveryProperties();
        properties.setEnabled(true);
        properties.setFrom("notifications@learnhub.test");
        properties.setFromName("LearnHub");
        properties.setReplyTo("support@learnhub.test");

        sender = new SmtpAccountNotificationSender(mailSender, properties);
        ReflectionTestUtils.setField(
                sender,
                "resetPasswordUrl",
                "https://learnhub.test/reset-password");
    }

    @Test
    void sendsInvitationOnlyToRequestedRecipient() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        boolean sent = sender.sendAccountInvitation(
                "student@example.com",
                "Sophie",
                "reset-token");

        ArgumentCaptor<MimeMessage> captor =
                ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage delivered = captor.getValue();

        assertThat(sent).isTrue();
        assertThat(delivered.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .containsExactly("student@example.com");
        assertThat(delivered.getAllRecipients()).hasSize(1);
        assertThat(delivered.getSubject()).isEqualTo("Bienvenue sur LearnHub");
        assertThat(delivered.getReplyTo()[0].toString())
                .isEqualTo("support@learnhub.test");
    }

    @Test
    void reportsSmtpFailureWithoutBreakingBusinessOperation() {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP indisponible"))
                .when(mailSender)
                .send(mimeMessage);

        boolean sent = sender.sendRequestReceived(
                "student@example.com",
                "Sophie");

        assertThat(sent).isFalse();
    }

    @Test
    void doesNotContactSmtpWhenEmailIsDisabled() {
        properties.setEnabled(false);

        boolean sent = sender.sendRequestRejected(
                "student@example.com",
                "Sophie");

        assertThat(sent).isFalse();
        verify(mailSender, never()).createMimeMessage();
    }
}

package sae.learnhub.learnhub.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mail")
@Getter
@Setter
public class MailDeliveryProperties {

    private boolean enabled;
    private String from = "no-reply@learnhub.local";
    private String fromName = "LearnHub";
    private String replyTo;
}

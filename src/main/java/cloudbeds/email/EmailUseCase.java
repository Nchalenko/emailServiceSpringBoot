package cloudbeds.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EmailUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(EmailUseCase.class);
    private final EmailService emailService;

    public EmailUseCase() {
        this.emailService = new SendGridEmailService();
    }

    void send(@Email String recipientEmail, String subject, String message) {

        EmailCmd email = new EmailCmd();
        email.setRecipient(recipientEmail);
        email.setSubject(subject);
        email.setTextBody(message);

        this.emailService.send(email);

        LOG.info("Sending email to {}: {} at {}", recipientEmail, subject, new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
    }
}

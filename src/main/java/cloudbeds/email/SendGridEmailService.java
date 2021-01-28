package cloudbeds.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SendGridEmailService implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(SendGridEmailService.class);

    @Value("${SENDGRID_APIKEY}")
    protected String apiKey;

    @Value("${SENDGRID_FROM_EMAIL}")
    protected String fromEmail;

    public SendGridEmailService() {
        this.apiKey = System.getenv("SENDGRID_APIKEY");
        this.fromEmail = System.getenv("SENDGRID_FROM_EMAIL");
    }

    protected Content contentOfEmail(Email email) {
        if ( email.getTextBody() !=null ) {
            return new Content("text/plain", email.getTextBody());
        }
        if ( email.getHtmlBody() !=null ) {
            return new Content("text/html", email.getHtmlBody());
        }
        return null;
    }

    @Override
    public void send(@NotNull @Valid Email email) {
        Personalization personalization = new Personalization();
        personalization.setSubject(email.getSubject());

        com.sendgrid.helpers.mail.objects.Email to = new com.sendgrid.helpers.mail.objects.Email(email.getRecipient());
        personalization.addTo(to);

        if ( email.getCc() != null ) {
            for ( String cc : email.getCc() ) {
                com.sendgrid.helpers.mail.objects.Email ccEmail = new com.sendgrid.helpers.mail.objects.Email();
                ccEmail.setEmail(cc);
                personalization.addCc(ccEmail);
            }
        }

        if ( email.getBcc()  != null ) {
            for ( String bcc : email.getBcc() ) {
                com.sendgrid.helpers.mail.objects.Email bccEmail = new com.sendgrid.helpers.mail.objects.Email();
                bccEmail.setEmail(bcc);
                personalization.addBcc(bccEmail);
            }
        }

        if (email.getSendAt() != null) {
            Long sendAt = email.getSendAt();
            Long currentTime = Instant.now().getEpochSecond();

            if (sendAt > currentTime) {
                personalization.setSendAt(sendAt);
            }
        }

        Mail mail = new Mail();
        com.sendgrid.helpers.mail.objects.Email from = new com.sendgrid.helpers.mail.objects.Email();
        from.setEmail(fromEmail);
        mail.from = from;
        mail.addPersonalization(personalization);
        Content content = contentOfEmail(email);
        mail.addContent(content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            if (LOG.isInfoEnabled()) {
                LOG.info("Status Code: {}", response.getStatusCode());
                Long sendAt = mail.getPersonalization().get(0).sendAt();

                if (sendAt > 0) {
                    Date date = new Date(sendAt * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String dateString = sdf.format(date);

                    LOG.info("MAIL WILL BE DELIVERED at: {}; to: {}; subject: {}", dateString, email.getRecipient(), email.getSubject());
                } else {
                    LOG.info("MAIL WAS SEND: to: {}; subject: {}", email.getRecipient(), email.getSubject());
                }
            }
        } catch (IOException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error(ex.getMessage());
            }
        }
    }
}

//      curl -X "POST" "http://localhost:8080/send" \
//        -H 'Content-Type: application/json; charset=utf-8' \
//        -d $'{
//        "subject": "Test Email",
//        "recipient": "nikita.chalenko@cloudbeds.com",
//        "textBody": "Foo",
//        "sendAt": "1611689052"
//        }'
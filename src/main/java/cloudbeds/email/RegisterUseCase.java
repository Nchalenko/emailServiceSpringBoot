package cloudbeds.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Configuration
public class RegisterUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterUseCase.class);

    protected final TaskScheduler taskScheduler;
    protected final EmailUseCase emailUseCase;

    public RegisterUseCase(EmailUseCase emailUseCase, TaskScheduler taskScheduler) {
        this.emailUseCase = emailUseCase;
        this.taskScheduler = taskScheduler;
    }

    public void register(String email, String subject) {
        LOG.info("Saving new scheduled email for {} with subject {} at {}", email, subject, new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
        scheduleFollowupEmail(email, subject, "Welcome to new CloudBeds email service");
    }

    private void scheduleFollowupEmail(String email, String subject, String message) {
        EmailTask task = new EmailTask(emailUseCase, email, subject, message);

        Calendar date = Calendar.getInstance();
        taskScheduler.schedule(task, new Date(date.getTimeInMillis() + (2 * 60000)));
    }
}

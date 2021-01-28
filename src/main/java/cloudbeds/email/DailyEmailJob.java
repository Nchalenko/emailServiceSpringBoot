package cloudbeds.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DailyEmailJob {

    private static final Logger LOG = LoggerFactory.getLogger(DailyEmailJob.class);
    protected final EmailUseCase emailUseCase;

    public DailyEmailJob(EmailUseCase emailUseCase) {
        this.emailUseCase = emailUseCase;
    }

    @Scheduled(cron = "0 30 4 1/1 * ?")
    void execute() {
        emailUseCase.send("nick.chalenko@gmail.com", "Daily Test Subject", "Daily Test Message");
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 500)
    void executeEveryTen() {
//        emailUseCase.send("nick.chalenko@gmail.com", "Every 10 min", "Daily Test Message");
        LOG.info("Job every 10 seconds: {}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date()));
    }
}

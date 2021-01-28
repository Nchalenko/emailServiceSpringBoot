package cloudbeds.email;

public class EmailTask implements Runnable {

    private String email;
    private String subject;
    private String message;
    private EmailUseCase emailUseCase;

    public EmailTask(EmailUseCase emailUseCase, String email, String subject, String message) {
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.emailUseCase = emailUseCase;
    }

    @Override
    public void run() {
        emailUseCase.send(this.email, this.subject, this.message);
    }

}

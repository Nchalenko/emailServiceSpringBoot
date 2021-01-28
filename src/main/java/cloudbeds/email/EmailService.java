package cloudbeds.email;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface EmailService {
    void send(@NotNull @Valid Email email);
}
package cloudbeds.controller;

import cloudbeds.email.EmailCmd;
import cloudbeds.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController("/mail")
public class MailController {

    private final EmailService emailService;

    @Autowired
    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String,Object>> send(@RequestBody @Valid EmailCmd cmd) {
        emailService.send(cmd);

        Map<String, Object> message = new HashMap<>();
        Map<String, Object> json = new HashMap<>();
        message.put("code", 202);
        json.put("success", true);
        json.put("message", "OK");

        message.putAll(json);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
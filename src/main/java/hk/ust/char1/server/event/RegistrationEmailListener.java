package hk.ust.char1.server.event;

import hk.ust.char1.server.auxilary.EmailSender;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RegistrationEmailListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    private final UserDetailsService userService;

    private final EmailSender emailSender;

    @Autowired
    public RegistrationEmailListener(UserDetailsService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationSuccessEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user,token);
        String recipient = user.getEmail();
        String subject = "[noreply] Registration Confirmation";
        String url = event.getAppUrl() + "confirmRegistration?token=" + token;
        String message = "Dear "+ user.getUsername() +", \n\n Thank you for registering on MatchaHouse.\n\nPlease click on the link below to activate your account:\n";
        emailSender.sendEmail(recipient,subject, message, url);
    }
}

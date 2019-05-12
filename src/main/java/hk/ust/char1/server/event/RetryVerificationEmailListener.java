package hk.ust.char1.server.event;

import hk.ust.char1.server.auxilary.EmailSender;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RetryVerificationEmailListener implements ApplicationListener<OnVerificationRetryEvent> {

    private final UserDetailsService userService;

    private final EmailSender emailSender;

    @Autowired
    public RetryVerificationEmailListener(UserDetailsService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @Override
    public void onApplicationEvent(OnVerificationRetryEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnVerificationRetryEvent event) {
        String token = UUID.randomUUID().toString();
        userService.updateVerificationToken(event.getUser(), token);
        String subject = "[noreply] Verification Re-confirmation";
        String url = event.getAppUrl() + "confirmRegistration?token=" + token;
        String message = "Dear "+ event.getUser().getUsername() +", \n\n The previous verification link has expired. A new link is sent to you as a replacement.\n Please click on the link below to activate your account: \n";

        emailSender.sendEmail(event.getUser().getEmail(),subject,message, url);
    }
}

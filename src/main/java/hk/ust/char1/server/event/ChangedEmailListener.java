package hk.ust.char1.server.event;

import hk.ust.char1.server.auxilary.EmailSender;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ChangedEmailListener implements ApplicationListener<OnEmailChangeEvent> {

    private final UserDetailsService userService;

    private final EmailSender emailSender;

    @Autowired
    public ChangedEmailListener(UserDetailsService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }


    @Override
    public void onApplicationEvent(OnEmailChangeEvent event) {
        this.resendEmail(event);
    }

    private void resendEmail(OnEmailChangeEvent event) {
        VerificationToken token = userService.findVerificationTokenByUsername(event.getUser().getUsername());
        String subject = "[noreply] Verification due to Email Changes";
        String url = event.getAppUrl() + "confirmRegistration?token=" + token;
        String message = "Dear "+ event.getUser().getUsername() + ", \n\nAs requested, the email recorded in our database has been changed. A new link is sent to you for you to confirm the changes. \n\nPlease click on the link below to activate your account.\n";

        emailSender.sendEmail(event.getUser().getEmail(),subject,message,url);
    }
}

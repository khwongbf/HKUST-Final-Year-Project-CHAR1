package hk.ust.char1.server.auxilary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    private final MailSender mailSender;

    @Autowired
    public EmailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String recipient, String subject, String message, String url){
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("khwongbf@connect.ust.hk");
        email.setTo(recipient);
        email.setSubject(subject);

        //TODO: Change the middle String to the home URL of the server's endpoint for access
        email.setText(message + "http://ec2-3-85-135-244.compute-1.amazonaws.com:5000/" + url);
        mailSender.send(email);
    }
}

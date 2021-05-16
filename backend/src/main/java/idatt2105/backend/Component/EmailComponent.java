package idatt2105.backend.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Component for sending emails
 */
@Component
public class EmailComponent {
    @Autowired(required = false)
    private JavaMailSender emailSender;

}

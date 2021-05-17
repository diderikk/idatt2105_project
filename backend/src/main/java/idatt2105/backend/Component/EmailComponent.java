package idatt2105.backend.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Component for sending emails
 */
@Component
public class EmailComponent {
    @Autowired(required = false)
    private JavaMailSender emailSender;

    public void sendPassword(String email, String password){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to BookThatRoom");
        message.setText("Hello and welcome to BookThatRoom!\n\n" + 
        "You have now been registered with a user to use when booking rooms.\n" + 
        "All you need to do is log in to https://idatt2105-project-diderikk.vercel.app with the following credentials: \n"+
        "Email: " + email + "\n" +
        "Password: " + password + "\n\n" + 
        "Best regards.\n BookThatRoom"
        );
    }

}

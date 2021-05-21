package idatt2105.backend.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import idatt2105.backend.Model.Reservation;

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
        "Best regards.\nBookThatRoom"
        );

        if(emailSender != null) emailSender.send(message);
    }

    public void sendReservationDeleted(Reservation reservation, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        String room = (!reservation.getSections().isEmpty()) ? reservation.getSections().get(0).getRoom().getRoomCode() : "No Room";
        message.setSubject("Deleted reservation in room: " + room);
        message.setText("Your reservation in room " + room + getTimeAndDateString(reservation.getStartTime(), reservation.getEndTime()) +
        " has been deleted\n\n" + 
        "Hope this bring no inconvenience!\n" +
        "Best regards.\nBookThatRoom");

        if(emailSender != null) emailSender.send(message);
    }

    private String getTimeAndDateString(LocalDateTime startTime, LocalDateTime endTime){
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String format1 = startTime.format(dateTimeFormat);
        String format2 = endTime.format(dateTimeFormat);

        return " from " + format1 + " to " + format2;
    }


}

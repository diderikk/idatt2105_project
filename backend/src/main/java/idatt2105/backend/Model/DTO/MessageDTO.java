package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import idatt2105.backend.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private long userId;
    private String firstName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeSent;
    private String text;

    public MessageDTO(Message message){
        if(message.getUser() != null) this.userId = message.getUser().getUserId();
        if(message.getUser() != null) this.firstName = message.getUser().getFirstName();
        this.timeSent = message.getTimeSent();
        this.text = message.getText();
    }
}

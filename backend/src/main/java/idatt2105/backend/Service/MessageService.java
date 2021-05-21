package idatt2105.backend.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Message;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.DTO.MessageDTO;
import idatt2105.backend.Repository.MessageRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.UserRepository;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Saves a message sent from client, if both room and user exists
     * @param roomCode
     * @param message, from client
     * @return boolean value if it was saved
     */
    public boolean saveMessage(String roomCode, MessageDTO message){
        Optional<Room> optionalRoom = roomRepository.findById(roomCode);
        Optional<User> optionalUser = userRepository.findById(message.getUserId());

        if(optionalRoom.isPresent() && optionalUser.isPresent()){
            Message newMessage = new Message();
            newMessage.setUser(optionalUser.get());
            newMessage.setRoom(optionalRoom.get());
            newMessage.setTimeSent(message.getTimeSent());
            newMessage.setText(message.getText());
            messageRepository.save(newMessage);
            return true;
        }

        return false;
    }
}

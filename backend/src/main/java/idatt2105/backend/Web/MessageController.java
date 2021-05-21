package idatt2105.backend.Web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import idatt2105.backend.Model.DTO.MessageDTO;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Service.MessageService;

@Controller
@CrossOrigin(originPatterns = "*",allowCredentials = "true", allowedHeaders = "*")
@Profile("!test")
public class MessageController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/{room_code}")
    public void connectChat(@DestinationVariable("room_code") String roomCode, @Payload MessageDTO payload){
        LOGGER.info("connectChat(@DestinationVariable(roomCode) long roomCode, @Payload MessageDTO payload) with roomCode {}", roomCode);
        if(roomRepository.findById(roomCode).isPresent()){
            simpMessagingTemplate.convertAndSend("/api/v1/chat/"+ roomCode +"/messages", payload);
            messageService.saveMessage(roomCode, payload);
        }
    }

}

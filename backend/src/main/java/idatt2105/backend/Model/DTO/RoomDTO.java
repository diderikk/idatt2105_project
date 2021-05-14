package idatt2105.backend.Model.DTO;

import java.util.List;
import java.util.stream.Collectors;

import idatt2105.backend.Model.Room;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomDTO {
    private String roomCode;
    private List<SectionDTO> sections;

    public RoomDTO(Room room) {
        roomCode = room.getRoomCode();
        if(room.getSections() != null) sections = room.getSections().stream().map(section -> new SectionDTO(section)).collect(Collectors.toList());
    }
}

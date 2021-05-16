package idatt2105.backend.Model.DTO;

import java.util.List;
import java.util.stream.Collectors;

import idatt2105.backend.Model.Room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDTO {
    private String roomCode;
    private List<GETSectionDTO> sections;

    public RoomDTO(Room room) {
        roomCode = room.getRoomCode();
        if(room.getSections() != null) sections = room.getSections().stream().map(section -> new GETSectionDTO(section)).collect(Collectors.toList());
    }
}

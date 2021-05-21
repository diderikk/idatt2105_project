package idatt2105.backend.Model.DTO.Room;

import java.util.List;
import java.util.stream.Collectors;

import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GETRoomDTO {
    private String roomCode;
    private List<GETSectionDTO> sections;

    public GETRoomDTO(Room room) {
        roomCode = room.getRoomCode();
        if(room.getSections() != null) sections = room.getSections().stream().map(section -> new GETSectionDTO(section)).collect(Collectors.toList());
    }
}

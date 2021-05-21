package idatt2105.backend.Model.DTO.Room;

import java.util.List;

import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POSTRoomDTO {
    private String roomCode;
    private List<POSTSectionDTO> sections;
}

package idatt2105.backend.Model.DTO;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomDTO {
    private String roomCode;
    private List<SectionDTO> sections;
}

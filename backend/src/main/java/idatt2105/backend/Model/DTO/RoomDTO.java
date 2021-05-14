package idatt2105.backend.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDTO {
    private String roomCode;
    private List<GETSectionDTO> sections;
}

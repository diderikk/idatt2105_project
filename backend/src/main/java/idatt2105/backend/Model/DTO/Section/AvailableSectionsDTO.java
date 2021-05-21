package idatt2105.backend.Model.DTO.Section;

import java.util.List;

import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSectionsDTO {
    private List<GETRoomDTO> rooms;
    private List<Long> idsOfAvailableSections;
}

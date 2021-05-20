package idatt2105.backend.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSectionsDTO {
    private List<GETRoomDTO> rooms;
    private List<AvailableSectionDTO> availableSections;
}

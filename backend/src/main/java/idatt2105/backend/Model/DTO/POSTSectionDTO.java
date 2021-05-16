package idatt2105.backend.Model.DTO;

import idatt2105.backend.Model.Section;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POSTSectionDTO {
    private String sectionName;
    private String roomCode;

    public POSTSectionDTO(Section section) {
        sectionName = section.getSectionName();
        if(section.getRoom() != null) roomCode = section.getRoom().getRoomCode();
    }
}

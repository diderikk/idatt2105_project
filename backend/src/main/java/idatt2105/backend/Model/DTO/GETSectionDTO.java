package idatt2105.backend.Model.DTO;

import idatt2105.backend.Model.Section;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GETSectionDTO {
    private long sectionId;
    private String roomCode;

    public GETSectionDTO(Section section) {
        sectionId = section.getSectionId();
        if(section.getRoom() != null) roomCode = section.getRoom().getRoomCode();
    }
}

package idatt2105.backend.Model.DTO;

import idatt2105.backend.Model.Section;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionDTO {
    private long sectionId;
    private String roomCode;

    public SectionDTO(Section section) {
        sectionId = section.getSectionId();
        if(section.getRoom() != null) roomCode = section.getRoom().getRoomCode();
    }
}

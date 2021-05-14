package idatt2105.backend.Model.DTO;

import idatt2105.backend.Model.Section;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POSTSectionDTO {
    private long sectionId;

    public POSTSectionDTO(Section section) {
        sectionId = section.getSectionId();
    }
}

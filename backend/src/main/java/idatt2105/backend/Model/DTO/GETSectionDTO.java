package idatt2105.backend.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GETSectionDTO {
    private long sectionId;
    private String roomCode;
}

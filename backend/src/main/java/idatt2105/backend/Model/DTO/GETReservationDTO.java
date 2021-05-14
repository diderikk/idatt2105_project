package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GETReservationDTO {
    private long reservationId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String reservationText;
    private int amountOfPeople;
    private long userId;
    private List<GETSectionDTO> sections;
}

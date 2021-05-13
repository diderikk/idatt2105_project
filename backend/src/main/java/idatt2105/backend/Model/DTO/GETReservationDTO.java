package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;

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
}

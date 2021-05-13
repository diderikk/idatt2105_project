package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;

import idatt2105.backend.Model.Reservation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationDTO {
    private long reservationId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String reservationText;

    public ReservationDTO(Reservation reservation) {
        reservationId = reservation.getReservationId();
        reservationStartTime = reservation.getReservationStartTime();
        reservationEndTime = reservation.getReservationEndTime();
        reservationText = reservation.getReservationText();
    }
}

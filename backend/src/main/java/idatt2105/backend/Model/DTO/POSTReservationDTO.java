package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import idatt2105.backend.Model.Reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class POSTReservationDTO {
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String reservationText;
    private int amountOfPeople;
    private List<POSTSectionDTO> sections;

    public POSTReservationDTO(Reservation reservation) {
        reservationStartTime = reservation.getReservationStartTime();
        reservationEndTime = reservation.getReservationEndTime();
        reservationText = reservation.getReservationText();
        amountOfPeople = reservation.getAmountOfPeople();
        sections = reservation.getSections().stream().map(section -> new POSTSectionDTO(section)).collect(Collectors.toList());
    }
}

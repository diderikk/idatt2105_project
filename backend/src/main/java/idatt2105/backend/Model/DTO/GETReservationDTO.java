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
public class GETReservationDTO {
    private long reservationId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
    private String reservationText;
    private int amountOfPeople;
    private long userId;
    private List<GETSectionDTO> sections;


    public GETReservationDTO(Reservation reservation){
        this.reservationId = reservation.getReservationId();
        this.reservationText = reservation.getReservationText();
        this.reservationStartTime = reservation.getReservationStartTime();
        this.reservationEndTime = reservation.getReservationEndTime();
        this.amountOfPeople = reservation.getAmountOfPeople();
        this.userId = reservation.getUser().getUserId();
        this.sections = reservation.getSections().stream()
        .map(section -> new GETSectionDTO(section.getSectionId(), section.getRoom().getRoomCode()))
        .collect(Collectors.toList());
    }
}

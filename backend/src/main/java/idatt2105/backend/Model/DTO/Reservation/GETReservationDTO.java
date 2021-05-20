package idatt2105.backend.Model.DTO.Reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.User.GETUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GETReservationDTO {
    private long reservationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reservationText;
    private int amountOfPeople;
    @JsonIgnoreProperties({"userId", "expirationDate", "isAdmin"})
    private GETUserDTO user;
    private List<GETSectionDTO> sections;


    public GETReservationDTO(Reservation reservation){
        this.reservationId = reservation.getReservationId();
        this.reservationText = reservation.getReservationText();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.amountOfPeople = reservation.getAmountOfPeople();
        if(reservation.getUser() != null) this.user = new GETUserDTO(reservation.getUser());
        if(reservation.getSections() != null) this.sections = reservation.getSections().stream()
        .map(section -> new GETSectionDTO(section.getSectionId(), section.getSectionName(), section.getRoom().getRoomCode()))
        .collect(Collectors.toList());
        else this.sections = new ArrayList<>();
    }
}

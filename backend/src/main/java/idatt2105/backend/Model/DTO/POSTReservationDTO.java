package idatt2105.backend.Model.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import idatt2105.backend.Model.Reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for reservation. 
 * Used for receiving information from frontend
 * using POST request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class POSTReservationDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    private String reservationText;
    private int amountOfPeople;
    private List<POSTSectionDTO> sections;

    public POSTReservationDTO(Reservation reservation) {
        startTime = reservation.getStartTime();
        endTime = reservation.getEndTime();
        reservationText = reservation.getReservationText();
        amountOfPeople = reservation.getAmountOfPeople();
        sections = reservation.getSections().stream().map(section -> new POSTSectionDTO(section)).collect(Collectors.toList());
    }
}

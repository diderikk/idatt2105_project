package idatt2105.backend.Model.DTO.Room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for loading statistics about a room
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatisticsDTO {
    private long totalHoursOfReservations;
}

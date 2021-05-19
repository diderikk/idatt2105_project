package idatt2105.backend.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for loading statistics about a user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private Long totalHoursOfReservations;
    private Integer totalReservations;
    private GETRoomDTO favouriteRoom;
    private GETSectionDTO favouriteSection;
}

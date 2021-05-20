package idatt2105.backend.Model.DTO.User;

import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
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

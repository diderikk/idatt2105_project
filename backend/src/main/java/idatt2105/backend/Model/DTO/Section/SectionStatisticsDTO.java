package idatt2105.backend.Model.DTO.Section;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for loading statistics about a section
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionStatisticsDTO {
    private long totalHoursOfReservations;
}
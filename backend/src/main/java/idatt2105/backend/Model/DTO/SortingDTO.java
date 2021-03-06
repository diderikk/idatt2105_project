package idatt2105.backend.Model.DTO;

import idatt2105.backend.Model.Enum.SortingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for sorting queries
 * passed from user
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SortingDTO {
    private SortingTypeEnum sortType;
    private String nameQuery;
    private String roomQuery;
}

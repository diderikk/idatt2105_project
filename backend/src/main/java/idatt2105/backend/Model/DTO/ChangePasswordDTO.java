package idatt2105.backend.Model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for password changing
 */
@Data
@NoArgsConstructor
public class ChangePasswordDTO {
   private long userId;
   private String oldPassword;
   private String newPassword; 
}

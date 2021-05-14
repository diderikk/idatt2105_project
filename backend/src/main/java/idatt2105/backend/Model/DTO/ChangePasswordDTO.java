package idatt2105.backend.Model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordDTO {
   private long userId;
   private String oldPassword;
   private String newPassword; 
}

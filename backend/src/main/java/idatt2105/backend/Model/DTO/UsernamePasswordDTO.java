package idatt2105.backend.Model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for user,
 * where only email and password is needed.
 */
@Data
@NoArgsConstructor
public class UsernamePasswordDTO {
    private String email;
    private String password;
}

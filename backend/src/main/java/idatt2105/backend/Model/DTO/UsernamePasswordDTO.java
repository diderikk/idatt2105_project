package idatt2105.backend.Model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernamePasswordDTO {
    private String email;
    private String password;
}

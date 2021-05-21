package idatt2105.backend.Model.DTO.User;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POSTUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate expirationDate;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
}

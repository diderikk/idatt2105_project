package idatt2105.backend.Model.DTO;

import java.time.LocalDate;

import idatt2105.backend.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GETUserDTO {
    private long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate expirationDate;
    private boolean isAdmin;

    public GETUserDTO(User user) {
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.expirationDate = user.getExpirationDate();
        this.isAdmin = user.isAdmin();
    }
}
package idatt2105.backend.Model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for storing information about a user
 * with userId as primary key
 */
@Entity
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phoneNumber;
    private String hash;
    private LocalDate expirationDate;
    private boolean isAdmin;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;
}
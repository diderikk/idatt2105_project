package idatt2105.backend.Model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for storing information about a reservation
 * with reservationId as primary key
 */
@Entity
@NoArgsConstructor
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reservationId;

    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
}

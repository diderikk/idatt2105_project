package idatt2105.backend.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for storing information about a room
 * with roomId as primary key
 */
@Entity
@NoArgsConstructor
@Data
public class Room {
    @Id
    private String roomName;
}
package idatt2105.backend.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
    private String roomCode;

    @OneToMany(mappedBy = "room")
    private List<Section> sections;
}
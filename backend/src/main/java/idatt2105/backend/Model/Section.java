package idatt2105.backend.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Section class for storing information about a section
 * with sectionId as primary key
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"sectionName","roomCode"}))
@NoArgsConstructor
@Data
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sectionId;
    private int seatAmount;
    private String sectionName;

    @ManyToOne
    @JoinColumn(name = "roomCode", referencedColumnName = "roomCode")
    private Room room;

    @ManyToMany(mappedBy = "sections", fetch = FetchType.EAGER)
    private List<Reservation> reservations;
}
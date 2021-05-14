package idatt2105.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query(value = "SELECT section.* FROM section WHERE section_name = ?1 AND room_code  = ?2", nativeQuery = true)
    Optional<Section> findSectionBySectionNameAndRoomCode(String sectionName, String roomCode);

    // Gets all reservation ids of a section. This can be used with reservationRepository deleteGivenReservations() method.
    @Query(value = "SELECT DISTINCT reservation_section.reservation_id FROM reservation_section WHERE reservation_section.section_id = ?1", nativeQuery = true)
    Optional<List<Long>> getAllReservationIdsOfSection(long sectionId);
}


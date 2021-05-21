package idatt2105.backend.Repository;

import java.time.LocalDateTime;
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
    List<Long> getAllReservationIdsOfSection(long sectionId);

    // Find top 5 most popular sections with most reservations
    @Query(value = "SELECT section.* FROM section " +
    "LEFT JOIN reservation_section ON reservation_section.section_id = section.section_id " +
    "GROUP BY section.section_id ORDER BY COUNT(section.section_id) DESC LIMIT 5", nativeQuery = true)
    List<Section> getTopSections();

    // Find total time this section was booked in the past
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(HOUR, reservation.start_time, reservation.end_time)) AS SumTime FROM reservation " +
    "JOIN reservation_section ON reservation_section.reservation_id = reservation.reservation_id " +
    "WHERE reservation_section.section_id = ?1 AND reservation.end_time <= NOW()", nativeQuery = true)
    Optional<Long> getTotalHoursBooked(long sectionId);

    @Query(value = "SELECT section.* FROM section WHERE section.section_id NOT IN (SELECT section.section_id FROM section JOIN reservation_section" +
    " ON (section.section_id = reservation_section.section_id) JOIN reservation" +
    " ON (reservation_section.reservation_id = reservation.reservation_id AND (?1 BETWEEN reservation.start_time AND reservation.end_time OR ?2 BETWEEN reservation.start_time" +
    " AND reservation.end_time OR (?1 <= reservation.start_time AND ?2 >= reservation.end_time))))", nativeQuery = true)
    List<Section> getAvailableSections(LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "SELECT section.* FROM section WHERE section.section_id NOT IN (SELECT section.section_id FROM section JOIN reservation_section" +
    " ON (section.section_id = reservation_section.section_id) JOIN reservation" +
    " ON (reservation_section.reservation_id = reservation.reservation_id AND reservation.reservation_id != ?3 AND (?1 BETWEEN reservation.start_time AND reservation.end_time OR ?2 BETWEEN reservation.start_time" +
    " AND reservation.end_time OR (?1 <= reservation.start_time AND ?2 >= reservation.end_time))))", nativeQuery = true)
    List<Section> getAvailableSections(LocalDateTime startTime, LocalDateTime endTime, Long reservationId);
}


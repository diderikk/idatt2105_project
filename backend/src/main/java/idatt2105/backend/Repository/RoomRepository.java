package idatt2105.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    // Find top 5 most popular rooms with most reservations
    @Query(value = "SELECT room.* FROM room LEFT JOIN section ON room.room_code = section.room_code LEFT JOIN reservation_section ON reservation_section.section_id = section.section_id GROUP BY room.room_code ORDER BY COUNT(room.room_code) DESC LIMIT 5", nativeQuery = true)
    Optional<List<Room>> getTopRooms();

    // Find total time this room was booked in the past
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(HOUR, reservation.start_time, reservation.end_time)) AS SumTime FROM reservation " +
    "JOIN reservation_section ON reservation_section.reservation_id = reservation.reservation_id " +
    "JOIN section ON section.section_id = reservation_section.section_id " +
    "WHERE section.room_code = ?1 AND reservation.end_time <= NOW()", nativeQuery = true)
    Optional<Float> getTotalHoursBooked(String roomCode);
}
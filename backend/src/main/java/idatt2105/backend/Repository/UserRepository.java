package idatt2105.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    // Find favourite (most booked) section of a user
    @Query(value = "SELECT section.* FROM section " +
    "JOIN reservation_section ON section.section_id = reservation_section.section_id " + 
    "JOIN reservation ON reservation.reservation_id = reservation_section.reservation_id " +
    "WHERE reservation.user_id = ?1 GROUP BY section.section_id ORDER BY COUNT(section.section_id) DESC LIMIT 1", nativeQuery = true)
    Section getFavouriteSectionOfUser(long userId);

    // Find favourite (most booked) room of a user
    @Query(value = "SELECT room.* FROM room " +
    "JOIN (SELECT section.* FROM section " +
    "JOIN reservation_section ON section.section_id = reservation_section.section_id " +
    "JOIN reservation ON reservation.reservation_id = reservation_section.reservation_id " +
    "WHERE reservation.user_id = ?1 GROUP BY section.section_id ORDER BY COUNT(section.section_id) DESC LIMIT 1) " +
    "AS selection ON room.room_code = selection.room_code ", nativeQuery = true)
    Room getFavouriteRoomOfUser(long userId); 
        
    // Find total time of all reservations user has had in the past
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(HOUR, reservation.start_time, reservation.end_time)) AS SumTime FROM reservation WHERE reservation.user_id = ?1 AND reservation.end_time <= NOW()", nativeQuery = true)
    Float getSumTimeInHoursOfAllUserReservations(long userId);

    // Find total reservations done by a user
    @Query(value = "SELECT COUNT(*) FROM reservation WHERE reservation.user_id = ?1", nativeQuery = true)
    Integer getResevationCountOfUser(long userId);
    
    // Find top 5 users with most reservations
    @Query(value = "SELECT user.* FROM user LEFT JOIN reservation ON reservation.user_id = user.user_id GROUP BY user.user_id ORDER BY COUNT(user.user_id) DESC LIMIT 5", nativeQuery = true)
    List<User> getTopUsers();
}
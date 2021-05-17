package idatt2105.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import idatt2105.backend.Model.Reservation;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // This is a modifying query which deletes all reservations given by reservationIds list
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reservation_section WHERE reservation_section.reservation_id IN (?1); DELETE FROM reservation WHERE reservation.reservation_id IN (?1)", nativeQuery = true)
    void deleteGivenReservations(List<Long> reservationIds);

    // Returns all reservations in the future sorted by date
    @Query(value = "SELECT reservation.* FROM reservation JOIN user "+
    "ON (reservation.user_id = user.user_id AND start_time > NOW() AND (user.first_name LIKE ?1 OR user.last_name LIKE ?1)) JOIN reservation_section "+
    "ON (reservation.reservation_id = reservation_section.reservation_id) JOIN section "+
    "ON (reservation_section.section_id = section.section_id AND section.room_code LIKE ?2) ORDER BY start_time DESC", nativeQuery = true)
    List<Reservation> getFutureReservationsSortedByDate(String nameQuery, String roomQuery);

    // Returns all reservations in the future sorted by amount of people
    @Query(value = "SELECT reservation.* FROM reservation JOIN user "+
    "ON (reservation.user_id = user.user_id AND start_time > NOW() AND (user.first_name LIKE ?1 OR user.last_name LIKE ?1)) JOIN reservation_section "+
    "ON (reservation.reservation_id = reservation_section.reservation_id) JOIN section "+
    "ON (reservation_section.section_id = section.section_id AND section.room_code LIKE ?2) WHERE start_time > NOW() ORDER BY amount_of_people DESC", nativeQuery = true)
    List<Reservation> getFutureReservationSortedByPeople(String nameQuery, String roomQuery);

    // Find total time of all reservations user has had in the past
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(MINUTE, reservation.start_time, reservation.end_time)) AS SumTime FROM reservation WHERE reservation.user_id = ?1 AND reservation.end_time <= NOW()", nativeQuery = true)
    Float getSumTimeInMinutesOfAllUserReservations(long userId);

    // Find total reservations done by a user
    @Query(value = "SELECT COUNT(*) FROM reservation WHERE reservation.user_id = ?1", nativeQuery = true)
    Integer getResevationCountOfUser(long userId);
}

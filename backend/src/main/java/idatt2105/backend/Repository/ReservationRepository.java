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
}

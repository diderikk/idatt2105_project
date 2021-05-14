package idatt2105.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import idatt2105.backend.Model.Reservation;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //"DELETE FROM Book b WHERE b.id IN (:ids)"
    @Query(value = "DELETE FROM reservation WHERE reservation.reservation_id IN (:?1)", nativeQuery = true)
    Optional<Boolean> deleteGivenReservations(List<Long> reservationIds);
}

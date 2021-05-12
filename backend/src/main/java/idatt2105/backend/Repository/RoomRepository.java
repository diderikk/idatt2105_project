package idatt2105.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
package idatt2105.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    // //"SELECT ba.bookId FROM BookAuthor ba JOIN Book b ON ba.bookId = b.id JOIN BookAuthor ba2 ON b.id = ba2.bookId WHERE ba2.authorId = ? GROUP BY ba.bookId HAVING count(ba.authorId) = 1"
    // @Query(value = "SELECT section.section_id FROM section JOIN room ON room.room_code = section.room_code JOIN room_section ON section.section_id = room_section.section_id WHERE room_section.room_code = ?1", nativeQuery = true)
    // Optional<List<Long>> getAllSectionsOfRoom(String roomCode);

    // //"DELETE FROM BookAuthor ba WHERE ba.authorId = ?"
    // @Query(value = "DELETE FROM room WHERE room_code = ?1", nativeQuery = true)
    // Optional<Room> removeAllRooms(String roomCode);

    // //"DELETE FROM Book b WHERE b.id IN (:ids)"
    // @Query(value = "DELETE FROM room WHERE room_code = ?1", nativeQuery = true)
    // Optional<Room> removeAllSections(List<String> roomCodes);
}
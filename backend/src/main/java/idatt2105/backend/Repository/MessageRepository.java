package idatt2105.backend.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT message.* FROM message WHERE room_code = ?1 AND time_sent >= ?2", nativeQuery = true)
    public List<Message> getMessages(String roomCode, LocalDateTime lastMessageTime);
}

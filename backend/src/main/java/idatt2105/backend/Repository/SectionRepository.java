package idatt2105.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query(value = "SELECT section.* FROM section WHERE section_name = ?1 AND room_code  = ?2", nativeQuery = true)
    Optional<Section> findSectionBySectionNameAndRoomCode(String sectionName, String roomCode);
}


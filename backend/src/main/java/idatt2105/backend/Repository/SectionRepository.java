package idatt2105.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import idatt2105.backend.Model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findSectionBySectionName(String sectionName);
}


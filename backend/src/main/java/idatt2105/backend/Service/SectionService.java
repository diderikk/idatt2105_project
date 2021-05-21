package idatt2105.backend.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.Section.SectionStatisticsDTO;
import idatt2105.backend.Repository.SectionRepository;
import javassist.NotFoundException;

@Service
public class SectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectionService.class);

    @Autowired
    private SectionRepository sectionRepository;
    
    /**
     * Returns list of all resevations of a section, given by sectionId
     * @param sectionId
     * @return List of reservationDTOs
     * @throws NoSectionFoundException
     * @throws NotFoundException
     */
    public List<GETReservationDTO> getReservationsOfSection(long sectionId) throws NotFoundException
    {
        LOGGER.info("getReservationsOfSection(long sectionId) called with sectionId: {}", sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        if(!sectionOptional.isPresent()) throw new NotFoundException("No section found with id " + sectionId);
        return sectionOptional.get().getReservations().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Finds top 5 most popular sections.
     * @return List of sections, empty list if no sections were found
     */
    public List<GETSectionDTO> getTopSections() {
        LOGGER.info("getTopSections() was called");
        List<Section> sections = sectionRepository.getTopSections();
        List<GETSectionDTO> sectionDTOs = new ArrayList<>();
        for(Section section : sections) {
            sectionDTOs.add(new GETSectionDTO(section));
        }
        return sectionDTOs;
    }

    /**
     * Get statistics about a section, given by section id.
     * Statistics such as total hours section has been booked/reserved.
     * @param sectionId
     * @return SectionStatisticsDTO
     * @throws NotFoundException
     */
    public SectionStatisticsDTO getStatistics(long sectionId) throws NotFoundException {
        LOGGER.info("getStatistics(long sectionId) called with sectionId: {}", sectionId);
        if(!sectionRepository.existsById(sectionId)) {
            LOGGER.warn("Could not find section with id: {}. Throwing exception", sectionId);
            throw new NotFoundException("No section found with id: " + sectionId);
        }
        Optional<Long> totalHoursBookedOptional = sectionRepository.getTotalHoursBooked(sectionId);
        Long totalHoursBooked = 0L;
        if(totalHoursBookedOptional.isPresent()) {
            totalHoursBooked = totalHoursBookedOptional.get();
        }

        return new SectionStatisticsDTO(totalHoursBooked);
    }
}
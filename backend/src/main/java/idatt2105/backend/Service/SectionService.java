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
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.GETSectionDTO;
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
     * Get total time (in hours) a section has been booked before.
     * Section is determined by sectionId parameter.
     * @param sectionId
     * @return Float of total time in hours
     * @throws NotFoundException if section was not found
     */
    public Long getTotalTimeBooked(long sectionId) throws NotFoundException {
        LOGGER.info("getTotalTimeBooked(long sectionId) called with sectionId: {}", sectionId);
        Optional<Long> sumOptional = sectionRepository.getTotalHoursBooked(sectionId);
        if(!sumOptional.isPresent()) {
            throw new NotFoundException("No section found with id: " + sectionId);
        }
        return sumOptional.get();
    }
}
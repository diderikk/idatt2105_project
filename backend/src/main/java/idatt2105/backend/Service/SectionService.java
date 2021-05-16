package idatt2105.backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.GETReservationDTO;
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
        //Return null if no section is present
        if(!sectionOptional.isPresent()) throw new NotFoundException("No section found with id " + sectionId);
        return sectionOptional.get().getReservations().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }
}
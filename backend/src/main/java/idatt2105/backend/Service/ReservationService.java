package idatt2105.backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Component.EmailComponent;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Model.Enum.SortingTypeEnum;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;
import javassist.NotFoundException;

@Service
public class ReservationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired(required = false)
    private EmailComponent emailComponent;

    /**
     * Returns all reserations in the database
     * @return List of GETReservationDTOs
     */
    public List<GETReservationDTO> getReservations(){
        LOGGER.info("getReservations() called");
        return reservationRepository.findAll().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Returns a reservation from database based on reservationId
     * @param reservationId
     * @return GETReservationDTO
     * @throws NotFoundException if reservation not found
     */
    public GETReservationDTO getReservation(long reservationId) throws NotFoundException{
        LOGGER.info("getReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(!optionalReservation.isPresent()){
            throw new NotFoundException("No reservation found with id: " + reservationId);
        }
        return new GETReservationDTO(optionalReservation.get());
    }

    /**
     * Gets sorted and filtered reservation list from database through queries and returns a list
     * @param dto 
     * @return List of GETReservationDTOs
     */
    public List<GETReservationDTO> getSortedAndFilteredReservations(SortingDTO dto){
        List<Reservation> reservations;
        String nameQuery = dto.getNameQuery() + "%";
        String roomQuery = "%" + dto.getRoomQuery() + "%";

        if(dto.getSortType() == SortingTypeEnum.AMOUNT_OF_PEOPLE) 
            reservations = reservationRepository.getFutureReservationSortedByPeople(nameQuery,roomQuery);
        else
            reservations = reservationRepository.getFutureReservationsSortedByDate(nameQuery,roomQuery);
        
        return reservations.stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Edits a reservation specified by reservationId, using information
     * from given POSTReservationDTO object. Returns edited reservation.
     * @param reservationId
     * @param dto
     * @return GETReservationDTO
     * @throws NotFoundException if no reservation or specified section found
     */
    public GETReservationDTO editReservation(long reservationId, POSTReservationDTO dto) throws NotFoundException {
        LOGGER.info("editReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(!optionalReservation.isPresent()){
            throw new NotFoundException("No reservation found with id: " + reservationId);
        }
        Reservation reservation = optionalReservation.get();
        reservation.setAmountOfPeople(dto.getAmountOfPeople());
        if(dto.getStartTime() != null) reservation.setStartTime(dto.getStartTime());
        if(dto.getEndTime() != null) reservation.setEndTime(dto.getEndTime());
        if(dto.getReservationText() != null) reservation.setReservationText(dto.getReservationText());
        if(dto.getSections() != null && !dto.getSections().isEmpty()) {
            for(POSTSectionDTO sectionDTO : dto.getSections()){
                Optional<Section> optionalSection = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
                if(optionalSection.isPresent()){
                    reservation.getSections().add(optionalSection.get());
                }
                else {
                    throw new NotFoundException("No section found with sectionName: " 
                        + sectionDTO.getSectionName() + ", and roomCode: " 
                        + sectionDTO.getRoomCode());
                }
            }
        }
        return new GETReservationDTO(reservation);
    }

    /**
     * Deletes a reservation specified by reservationId
     * @param reservationId
     * @return true if reservation was deleted and no longer exist in the database
     * @throws NotFoundException if no reservation found
     */
    public boolean deleteReservation(long reservationId) throws NotFoundException{
        LOGGER.info("deleteReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(!optionalReservation.isPresent()){
            throw new NotFoundException("No reservation found with id: " + reservationId);
        }
        Reservation reservation = optionalReservation.get();
        reservation.getSections().clear();
        reservation = reservationRepository.save(reservation);
        reservationRepository.delete(reservation);

        if(emailComponent != null) emailComponent.sendReservationDeleted(reservation, reservation.getUser().getEmail());

        return !reservationRepository.existsById(reservationId);
    }
}

package idatt2105.backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Component.EmailComponent;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Reservation.POSTReservationDTO;
import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
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
        return reservationRepository.findAll().stream().filter(reservation -> reservation.getStartTime().isAfter(LocalDateTime.now()))
        .map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
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
     * @throws SectionAlreadyBookedException
     */
    public GETReservationDTO editReservation(long reservationId, POSTReservationDTO dto) throws NotFoundException, SectionAlreadyBookedException {
        LOGGER.info("editReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(!optionalReservation.isPresent()){
            throw new NotFoundException("No reservation found with id: " + reservationId);
        }
        Reservation reservation = optionalReservation.get();
        reservation.setAmountOfPeople(dto.getAmountOfPeople());
        
        if(dto.getReservationText() != null) reservation.setReservationText(dto.getReservationText());
        if(dto.getSections() != null && !dto.getSections().isEmpty()) {
            for(POSTSectionDTO sectionDTO : dto.getSections()){
                reservation.setSections(new ArrayList<>());
                Optional<Section> optionalSection = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
                if(optionalSection.isPresent()){
                    if(checkIfSectionIsBooked(sectionDTO, dto, reservationId)) throw new SectionAlreadyBookedException("Section is already booked/reserved for the given time period");
                    reservation.getSections().add(optionalSection.get());
                }
                else {
                    throw new NotFoundException("No section found with sectionName: " 
                        + sectionDTO.getSectionName() + ", and roomCode: " 
                        + sectionDTO.getRoomCode());
                }
            }
        }
        if(dto.getStartTime() != null && dto.getStartTime().isBefore(dto.getEndTime())) reservation.setStartTime(dto.getStartTime());
        else throw new IllegalArgumentException("Start time must be before endTime");
        if(dto.getEndTime() != null) reservation.setEndTime(dto.getEndTime());
        return new GETReservationDTO(reservationRepository.save(reservation));
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
        try{
            if(emailComponent != null) emailComponent.sendReservationDeleted(reservation, reservation.getUser().getEmail());
        }catch (Exception ex){
            LOGGER.info("deleteReservation(long reservationId) called with bad email: {}",reservation.getUser().getEmail());
        }

        return !reservationRepository.existsById(reservationId);
    }


    /**
     * Checks if section is already reserved.
     * n² complexity.
     * @param sectionDTO
     * @param reservationDTO
     * @return true if reservation already exists during specified times
     * @throws NotFoundException
     */
    private boolean checkIfSectionIsBooked(POSTSectionDTO sectionDTO, POSTReservationDTO reservationDTO, long reservationId) throws NotFoundException {
        Optional<Section> sectionOptional = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
        if(!sectionOptional.isPresent()) throw new NotFoundException("Section not found with name " + sectionDTO.getSectionName());
        Section section = sectionOptional.get();
        if(section.getReservations() == null) throw new NullPointerException("Section tries to access reservation list that is null");
        for (Reservation reservation : section.getReservations()) {
            if(reservation.getReservationId() == reservationId) continue;
            if((reservationDTO.getStartTime().isEqual(reservation.getStartTime()))
                || (reservationDTO.getEndTime().isEqual(reservation.getEndTime()))
                || (reservationDTO.getStartTime().isAfter(reservation.getStartTime()) && reservationDTO.getStartTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getEndTime().isAfter(reservation.getStartTime()) && reservationDTO.getEndTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getStartTime().isBefore(reservation.getStartTime()) && reservationDTO.getEndTime().isAfter(reservation.getEndTime()))) {
                return true;
            }
        }
        return false;
    }
}

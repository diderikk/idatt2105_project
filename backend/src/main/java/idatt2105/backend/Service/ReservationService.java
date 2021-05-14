package idatt2105.backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;

@Service
public class ReservationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public List<GETReservationDTO> getReservations(){
        LOGGER.info("getReservations() called");
        return reservationRepository.findAll().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    public GETReservationDTO getReservation(long reservationId){
        LOGGER.info("getReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(optionalReservation.isPresent()){
            return new GETReservationDTO(optionalReservation.get());
        }
        return null;
    }

    public GETReservationDTO editReservation(long reservationId, POSTReservationDTO dto){
        LOGGER.info("editReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            reservation.setAmountOfPeople(dto.getAmountOfPeople());
            if(dto.getReservationStartTime() != null) reservation.setReservationStartTime(dto.getReservationStartTime());
            if(dto.getReservationEndTime() != null) reservation.setReservationEndTime(dto.getReservationEndTime());
            if(dto.getReservationText() != null) reservation.setReservationText(dto.getReservationText());
            if(dto.getSections() != null && !dto.getSections().isEmpty()) {
                for(POSTSectionDTO sectionDTO : dto.getSections()){
                    Optional<Section> optionalSection = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
                    if(optionalSection.isPresent()){
                        reservation.getSections().add(optionalSection.get());
                    }
                    else return null;
                }
            }
            return new GETReservationDTO(reservation);
        }
        return null;
    }

    public boolean deleteReservation(long reservationId){
        LOGGER.info("deleteReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            reservation.getSections().clear();
            reservation = reservationRepository.save(reservation);
            reservationRepository.delete(reservation);
            return true;
        }
        return false;
    }
}

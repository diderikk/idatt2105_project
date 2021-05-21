package idatt2105.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Reservation.POSTReservationDTO;
import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import idatt2105.backend.Model.Enum.SortingTypeEnum;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;
import javassist.NotFoundException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SectionRepository sectionRepository;

    private User user;
    private ChangePasswordDTO changePasswordDTO;
    private Reservation reservation;
    private Section section;
    private Room room;
    private POSTReservationDTO postReservationDTO;
    

    @BeforeEach
    public void setup(){
        user = new User();
        user.setAdmin(false);
        user.setEmail("test@test.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("12345678");
        user.setUserId(1L);
        user.setExpirationDate(null);

        reservation = new Reservation();
        reservation.setReservationId(1);
        reservation.setReservationText("reservationText");
        reservation.setAmountOfPeople(1);
        reservation.setUser(user);
        reservation.setStartTime(LocalDateTime.now().plusHours(10));

        user.setReservations(List.of(reservation));

        changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword("test");
        changePasswordDTO.setNewPassword("test");
        changePasswordDTO.setUserId(1);

        room = new Room();
        room.setRoomCode("roomCode");

        section = new Section();
        section.setSeatAmount(100);
        section.setSectionId(1);
        section.setReservations(List.of());
        section.setRoom(room);
        section.setSectionName("sectionName");
        ArrayList<Section> sections = new ArrayList<>();
        sections.add(section);
        reservation.setSections(sections);

        postReservationDTO = new POSTReservationDTO(LocalDateTime.now().plusHours(10),LocalDateTime.now().plusDays(1), "Edited", 20, List.of(new POSTSectionDTO(section.getSectionName(), section.getRoom().getRoomCode())));

        Mockito.lenient()
        .when(sectionRepository.findById(1L))
        .thenReturn(Optional.of(section));

        Mockito.lenient()
        .when(reservationRepository.save(any()))
        .thenReturn(reservation);

        Mockito.lenient()
        .when(reservationRepository.findById(1L))
        .thenReturn(Optional.of(reservation));

        Mockito.lenient()
        .when(reservationRepository.findAll())
        .thenReturn(List.of(reservation));

        Mockito.lenient()
        .when(sectionRepository.findSectionBySectionNameAndRoomCode("sectionName", "roomCode"))
        .thenReturn(Optional.of(section));

        Mockito.lenient()
        .when(reservationRepository.getFutureReservationsSortedByDate(anyString(), anyString()))
        .thenReturn(List.of(reservation));
    }

    @Test
    public void getReservation_ReservationExists_ReturnsListOfOne(){
        List<GETReservationDTO> reservations = reservationService.getReservations();

        assertEquals(1, reservations.size());
    }

    @Test
    public void getReservation_ReservationExists_ReturnsReservation() throws NotFoundException{
        GETReservationDTO tempReservation = reservationService.getReservation(reservation.getReservationId());
        assertEquals(reservation.getReservationId(), tempReservation.getReservationId());
        assertEquals(reservation.getAmountOfPeople(), tempReservation.getAmountOfPeople());
        assertEquals(reservation.getReservationText(), tempReservation.getReservationText());
    }

    @Test
    public void getReservation_ReservationDoesNotExist_ThrowsNotFoundException() throws NotFoundException{
        assertThrows(NotFoundException.class, () -> reservationService.getReservation(reservation.getReservationId()+2));
    }

    @Test
    public void getSortedAndFilteredReservations_CorrectInput_ReturnsListOfOneObject(){
        SortingDTO sortingDTO = new SortingDTO(SortingTypeEnum.DATE, "", "");
        List<GETReservationDTO> resultReservations = reservationService.getSortedAndFilteredReservations(sortingDTO);

        assertEquals(1, resultReservations.size());
    }

    @Test
    public void editReservation_CorrectInput_ReturnsEditedReservation() throws NotFoundException, SectionAlreadyBookedException{
        reservation.setSections(new ArrayList<>());
        GETReservationDTO returnDto = reservationService.editReservation(reservation.getReservationId(),postReservationDTO);

        assertEquals(reservation.getReservationId(), returnDto.getReservationId());
        assertEquals(postReservationDTO.getReservationText(), returnDto.getReservationText());
        assertEquals(postReservationDTO.getAmountOfPeople(), returnDto.getAmountOfPeople());
        assertEquals(1, returnDto.getSections().size());
    }

    @Test
    public void editReservation_UserDoesNotExist_ThrowsNotFoundException() throws NotFoundException{  
        assertThrows(NotFoundException.class, () -> reservationService.editReservation(-1,postReservationDTO));
    }

    @Test
    public void deleteReservation_CorrectInput_DeletesReservation() throws NotFoundException{
        boolean successful = reservationService.deleteReservation(reservation.getReservationId());
        assertEquals(true, successful);
    }

    @Test
    public void deleteReservation_WrongInput_DoesNotDeleteReservationAndThrowsNotFoundException() throws NotFoundException{
        assertThrows(NotFoundException.class, () -> reservationService.deleteReservation(reservation.getReservationId()+1));
    }
}

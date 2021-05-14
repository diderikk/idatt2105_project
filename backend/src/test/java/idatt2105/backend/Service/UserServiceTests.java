package idatt2105.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.UserDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;
import idatt2105.backend.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private UserDTO userDTO;
    private ChangePasswordDTO changePasswordDTO;
    private Reservation reservation;
    private Section section;
    private Room room;
    

    @BeforeEach
    public void setup(){
        user = new User();
        user.setAdmin(false);
        user.setEmail("test@test.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("12345678");
        user.setUserId(1L);
        user.setHash(passwordEncoder.encode("test"));
        user.setExpirationDate(null);

        reservation = new Reservation();
        reservation.setReservationId(1);
        reservation.setReservationText("reservationText");
        reservation.setAmountOfPeople(1);
        reservation.setUser(user);

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
        reservation.setSections(List.of(section));

        userDTO = new UserDTO(2, "firstName", "lastName", "email", "phoneNumber", null, false);
        Mockito.lenient().when(userRepository.findById(1L))
        .thenReturn(Optional.of(user));

        Mockito.lenient().when(userRepository.save(any()))
        .thenReturn(user);

        Mockito.lenient()
        .when(passwordEncoder.encode(anyString()))
        .thenReturn("test");

        Mockito.lenient()
        .when(sectionRepository.findById(1L))
        .thenReturn(Optional.of(section));

        Mockito.lenient()
        .when(reservationRepository.save(any()))
        .thenReturn(reservation);

        Mockito.lenient()
        .when(userRepository.findUserByEmail("email"))
        .thenReturn(Optional.of(user));
    }

    @Test
    public void getUser_IdExists_UserIsCorrect(){
        UserDTO userDTO = userService.getUser(1L);
        assertNotNull(userDTO);
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getPhoneNumber(), userDTO.getPhoneNumber());
        assertEquals(user.isAdmin(), userDTO.isAdmin());
    }
    
    @Test
    public void getUser_IdDoesNotExist_UserIsNull(){
        UserDTO temp = userService.getUser(0L);
        assertNull(temp);
    }

    @Test
    public void createUser_InputIsCorrect_UserCreated(){
        UserDTO temp = userService.createUser(userDTO);
        assertNotNull(temp);
        assertEquals(user.getUserId(), temp.getUserId());
        assertEquals(userDTO.getFirstName(), temp.getFirstName());
        assertEquals(userDTO.getLastName(), temp.getLastName());
        assertEquals(userDTO.getEmail(), temp.getEmail());
        assertEquals(userDTO.getPhoneNumber(), temp.getPhoneNumber());
        assertEquals(userDTO.getExpirationDate(), temp.getExpirationDate());
    }

    @Test
    public void createUser_InputIsWrong_UserNotCreated(){
        userDTO.setFirstName(null);
        UserDTO temp = userService.createUser(userDTO);
        assertNull(temp);
    }

    @Test
    public void changePassword_PasswordIsCorrect_PasswordChanged(){
        Mockito.lenient().when(passwordEncoder.matches(any(), any()))
        .thenReturn(true);
        boolean successful = userService.changePassword(changePasswordDTO);
        assertEquals(true, successful);
    }

    @Test
    public void changePassword_OldPasswordIsWrong_PasswordNotChanged(){
        Mockito.lenient().when(passwordEncoder.matches(any(), any()))
        .thenReturn(false);
        boolean successful = userService.changePassword(changePasswordDTO);
        assertEquals(false, successful);
    }

    @Test
    public void getUserReservations_CorrectId_ReturnsListOfOneObject(){
        List<GETReservationDTO> userReservations = userService.getUserReservations(1);
        assertEquals(user.getReservations().size(), userReservations.size());
        assertEquals(reservation.getReservationText(), userReservations.get(0).getReservationText());
    }

    @Test
    public void getUserReservations_WrongId_ReturnsEmptyList(){
        List<GETReservationDTO> userReservations = userService.getUserReservations(0);
        assertEquals(0, userReservations.size());
    }

    @Test
    public void addUserReservation_CorrectInput_ReservationAdded(){
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO(1));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);
        dto = userService.addUserReservation(1, dto);

        assertNotNull(dto);
    }

    @Test
    public void addUserReservation_WrongSectionId_ReservationNotAdded(){
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO(2));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);
        dto = userService.addUserReservation(1, dto);

        assertNull(dto);
    }

    @Test
    public void addUserReservation_WrongUserId_ReservationNotAdded(){
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO(1));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);
        dto = userService.addUserReservation(2, dto);

        assertNull(dto);
    }

    @Test
    public void removeUserReservation_WrongUserId_ReturnsFalse(){
        boolean successful = userService.removeUserReservation(0, 1);
        assertEquals(false, successful);
    }

    @Test
    public void loadUserByEmail_CorrectInput_ReturnsObject(){
        UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userService.loadUserByUsername("email");
        assertNotNull(userSecurityDetails);
        assertEquals(user.getEmail(), userSecurityDetails.getEmail());
        assertEquals(user.getUserId(), userSecurityDetails.getUserId());
        assertEquals(1, userSecurityDetails.getAuthorities().size());
        assertEquals(user.getHash(), userSecurityDetails.getPassword());
    }

    @Test
    public void loadUserByUsername_WrongUserId_ExceptionsThrown(){
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("email1"));
    }



}

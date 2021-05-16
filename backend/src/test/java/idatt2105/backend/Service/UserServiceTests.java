package idatt2105.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.xml.bind.ValidationException;

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

import idatt2105.backend.Exception.EmailAlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
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
import javassist.NotFoundException;

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
    public void setup()
    {
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
        section.setSectionName("sectionName");
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

        Mockito.lenient()
        .when(sectionRepository.findSectionBySectionNameAndRoomCode("sectionName", "roomCode"))
        .thenReturn(Optional.of(section));
    }

    @Test
    public void getUser_IdExists_UserIsCorrect() throws NotFoundException
    {
        UserDTO userDTO = userService.getUser(1L);
        assertNotNull(userDTO);
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getPhoneNumber(), userDTO.getPhoneNumber());
        assertEquals(user.isAdmin(), userDTO.isAdmin());
    }
    
    @Test
    public void getUser_IdDoesNotExist_UserIsNull() throws NotFoundException
    {
        assertThrows(NotFoundException.class, () -> userService.getUser(0L));
    }

    @Test
    public void createUser_InputIsCorrect_UserCreated() throws EmailAlreadyExistsException
    {
        userDTO.setEmail("email1234");
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
    public void createUser_InputIsWrong_UserNotCreated() throws EmailAlreadyExistsException
    {
        UserDTO testDTO = new UserDTO(this.user);
        Mockito.lenient().when(userRepository.findUserByEmail(testDTO.getEmail()))
        .thenReturn(Optional.of(this.user));
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(testDTO));
    }

    @Test
    public void changePassword_PasswordIsCorrect_PasswordChanged() throws ValidationException, NotFoundException
    {
        Mockito.lenient().when(passwordEncoder.matches(any(), any()))
        .thenReturn(true);
        boolean successful = userService.changePassword(changePasswordDTO);
        assertEquals(true, successful);
    }

    @Test
    public void changePassword_OldPasswordIsWrong_PasswordNotChanged() throws ValidationException, NotFoundException
    {
        Mockito.lenient().when(passwordEncoder.matches(any(), any()))
        .thenReturn(false);
        assertThrows(ValidationException.class, () -> userService.changePassword(changePasswordDTO));
    }

    @Test
    public void getUserReservations_CorrectId_ReturnsListOfOneObject() throws NotFoundException
    {
        List<GETReservationDTO> userReservations = userService.getUserReservations(1);
        assertEquals(user.getReservations().size(), userReservations.size());
        assertEquals(reservation.getReservationText(), userReservations.get(0).getReservationText());
    }

    @Test
    public void getUserReservations_WrongId_ReturnsEmptyList() throws NotFoundException
    {
        assertThrows(NotFoundException.class, () -> userService.getUserReservations(0));
    }

    @Test
    public void addUserReservation_CorrectInput_ReservationAdded() throws NotFoundException, SectionAlreadyBookedException
    {
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO(section.getSectionName(),room.getRoomCode()));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);
        dto = userService.addUserReservation(1, dto);

        assertNotNull(dto);
    }

    @Test
    public void addUserReservation_WrongSectionId_ReservationNotAdded() throws NotFoundException, SectionAlreadyBookedException
    {
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO("fake", room.getRoomCode()));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);

        assertThrows(NotFoundException.class, () -> userService.addUserReservation(1, dto));
    }

    @Test
    public void addUserReservation_WrongUserId_ReservationNotAdded() throws NotFoundException, SectionAlreadyBookedException
    {
        user.setReservations(new ArrayList<>());
        List<POSTSectionDTO> tempSections = List.of(new POSTSectionDTO("sectionName", room.getRoomCode()));
        POSTReservationDTO dto = new POSTReservationDTO(null, null, "reservationText", 100, tempSections);

        assertThrows(NotFoundException.class, () -> userService.addUserReservation(2, dto));
    }

    @Test
    public void removeUserReservation_WrongUserId_ReturnsFalse() throws NotFoundException
    {
        assertThrows(NotFoundException.class, () -> userService.removeUserReservation(0, 1));
    }

    @Test
    public void loadUserByEmail_CorrectInput_ReturnsObject()
    {
        UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userService.loadUserByUsername("email");
        assertNotNull(userSecurityDetails);
        assertEquals(user.getEmail(), userSecurityDetails.getEmail());
        assertEquals(user.getUserId(), userSecurityDetails.getUserId());
        assertEquals(1, userSecurityDetails.getAuthorities().size());
        assertEquals(user.getHash(), userSecurityDetails.getPassword());
    }

    @Test
    public void loadUserByUsername_WrongUserId_ExceptionsThrown()
    {
        assertThrows(NoSuchElementException.class, () -> userService.loadUserByUsername("email1"));
    }
}

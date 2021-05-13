package idatt2105.backend.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.UserDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;
import idatt2105.backend.Repository.UserRepository;
import idatt2105.backend.Service.UserService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Section section;
    private Reservation reservation;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email");
        user.setExpirationDate(null);
        user.setHash(passwordEncoder.encode("hash"));
        user.setPhoneNumber("12345678");
        user.setAdmin(false);
        user.setReservations(new ArrayList<>());
        user = userRepository.save(user);

        section = new Section();
        section.setSeatAmount(10);
        section = sectionRepository.save(section);

        reservation = new Reservation();
        reservation.setAmountOfPeople(10);
        reservation.setReservationStartTime(LocalDateTime.now());
        reservation.setReservationEndTime(LocalDateTime.now().plusHours(2));
        reservation.setSections(List.of(section));
        reservation.setReservationText("reservationText");
        reservation.setUser(user);
        reservation = reservationRepository.save(reservation);

        user = userRepository.findById(user.getUserId()).get();


        

        userDTO = new UserDTO(-1, "firstName", "lastName", "email", "phoneNumber", null, false);

    }

    @AfterEach
    public void tearDown() {
        userService.removeUserReservation(user.getUserId(), reservation.getReservationId());
        reservationRepository.deleteAll();
        sectionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getUser_UserExists_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + user.getUserId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is((int) user.getUserId())))
                .andExpect(jsonPath("$.firstName", equalToIgnoringCase(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalToIgnoringCase(user.getLastName())))
                .andExpect(jsonPath("$.email", equalToIgnoringCase(user.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", equalToIgnoringCase(user.getPhoneNumber())));
    }

    @Test
    public void getUser_UserDoesNotExist_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/users/0")).andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_InputCorrect_CreatesUser() throws Exception {
        String userDTOString = objectMapper.writeValueAsString(userDTO);
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(userDTOString))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId", isA(Integer.class)))
                .andExpect(jsonPath("$.firstName", equalToIgnoringCase(userDTO.getFirstName())));
    }

    @Test
    public void createUser_BadInput_ReturnBadRequest() throws Exception {
        userDTO.setFirstName(null);
        String userDTOString = objectMapper.writeValueAsString(userDTO);
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(userDTOString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changePassword_CorrectInput_PasswordChanged() throws Exception{
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setUserId(user.getUserId());
        changePasswordDTO.setOldPassword("hash");
        changePasswordDTO.setNewPassword("newHash");
        String changePasswordDTOString = objectMapper.writeValueAsString(changePasswordDTO);

        mockMvc.perform(put("/api/v1/users/"+ user.getUserId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(changePasswordDTOString))
        .andExpect(status().isOk());

        changePasswordDTO.setOldPassword("newHash");
        changePasswordDTOString = objectMapper.writeValueAsString(changePasswordDTO);

        mockMvc.perform(put("/api/v1/users/"+ user.getUserId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(changePasswordDTOString))
        .andExpect(status().isOk());
    }

    @Test
    public void changePassword_WrongOldPassword_BadRequest() throws Exception{
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setUserId(user.getUserId());
        changePasswordDTO.setOldPassword("hash123");
        changePasswordDTO.setNewPassword("newHash");
        String changePasswordDTOString = objectMapper.writeValueAsString(changePasswordDTO);

        mockMvc.perform(put("/api/v1/users/"+ user.getUserId() + "/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(changePasswordDTOString))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserReservations_UserExists_ReturnsListOfOneObject() throws Exception{
        mockMvc.perform(get("/api/v1/users/"+user.getUserId()+"/reservations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].reservationId", is((int)reservation.getReservationId())))
        .andExpect(jsonPath("$.[0].reservationText", equalTo(reservation.getReservationText())))
        .andExpect(jsonPath("$.[0].amountOfPeople", is(reservation.getAmountOfPeople())));
    }

    @Test
    public void addUserReservation_InputCorrect_ReservationAdded() throws Exception{
        POSTReservationDTO dto = new POSTReservationDTO();
        dto.setAmountOfPeople(10);
        dto.setReservationText("test");
        dto.setSections(new ArrayList<>());
        String DTOString = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/api/v1/users/"+user.getUserId()+"/reservations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(DTOString))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationText", equalTo(dto.getReservationText())))
        .andExpect(jsonPath("$.amountOfPeople", is(dto.getAmountOfPeople())));
    }
}

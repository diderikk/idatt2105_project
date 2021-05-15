package idatt2105.backend.Web;

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
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;
import idatt2105.backend.Repository.UserRepository;
import idatt2105.backend.Service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerTest {
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
    private RoomRepository roomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Section section;
    private Reservation reservation;
    private Room room;
    private POSTReservationDTO postReservationDTO;

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

        room = new Room();
        room.setRoomCode("roomCode");
        room = roomRepository.save(room);

        section = new Section();
        section.setSeatAmount(10);
        section.setRoom(room);
        section.setSectionName("sectionName");
        section = sectionRepository.save(section);

        reservation = new Reservation();
        reservation.setAmountOfPeople(10);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(2));
        ArrayList<Section> sections = new ArrayList<>();
        sections.add(section);
        reservation.setSections(sections);
        reservation.setReservationText("reservationText");
        reservation.setUser(user);
        reservation = reservationRepository.save(reservation);

        user = userRepository.findById(user.getUserId()).get();

        postReservationDTO = new POSTReservationDTO(null, null, "Edited", 20, 
        List.of(new POSTSectionDTO(section.getSectionName(), section.getRoom().getRoomCode())));
    }

    @AfterEach
    public void tearDown() {
        userService.removeUserReservation(user.getUserId(), reservation.getReservationId());
        reservationRepository.deleteAll();
        sectionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getReservation_ReservationsExists_ReturnsListOfOne() throws Exception {
        mockMvc.perform(get("/api/v1/reservations")).andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].reservationId", equalTo((int) reservation.getReservationId())))
                .andExpect(jsonPath("$.[0].reservationText", equalTo(reservation.getReservationText())))
                .andExpect(jsonPath("$.[0].amountOfPeople", is(reservation.getAmountOfPeople())));
    }

    @Test
    public void getReservation_ReservationExists_ReturnsReservation() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/" + reservation.getReservationId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId", is((int) reservation.getReservationId())))
                .andExpect(jsonPath("$.reservationText", equalTo(reservation.getReservationText())))
                .andExpect(jsonPath("$.amountOfPeople", is(reservation.getAmountOfPeople())));
    }

    @Test
    public void getReservation_ReservationDoesNotExists_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/" + 0)).andExpect(status().isNotFound());
    }

    @Test
    public void editReservation_CorrectInput_EditsRequest() throws Exception {
        String dtoString = objectMapper.writeValueAsString(postReservationDTO);
        mockMvc.perform(post("/api/v1/reservations/"+ reservation.getReservationId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(dtoString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationId", is((int) reservation.getReservationId())))
        .andExpect(jsonPath("$.reservationText", equalTo(postReservationDTO.getReservationText())))
        .andExpect(jsonPath("$.amountOfPeople", is(postReservationDTO.getAmountOfPeople())))
        .andExpect(jsonPath("$.sections.[0].sectionId", is((int)section.getSectionId())));
    }

    @Test
    public void editReservation_WrongReservationId_ReturnsBadRequest() throws Exception{
        String dtoString = objectMapper.writeValueAsString(postReservationDTO);
        mockMvc.perform(post("/api/v1/reservations/"+ 0)
        .contentType(MediaType.APPLICATION_JSON)
        .content(dtoString))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void editReservation_WrongSection_ReturnsBadRequest() throws Exception{
        ArrayList<POSTSectionDTO> sectionDTOs = new ArrayList<>();
        sectionDTOs.add(new POSTSectionDTO("fake", "fake"));
        postReservationDTO.setSections(sectionDTOs);
        String dtoString = objectMapper.writeValueAsString(postReservationDTO);
        mockMvc.perform(post("/api/v1/reservations/"+ reservation.getReservationId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(dtoString))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteReservation_ReservationExists_DeletesReservation() throws Exception{
        mockMvc.perform(delete("/api/v1/reservations/" + reservation.getReservationId()))
        .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/reservations/" + reservation.getReservationId()))
        .andExpect(status().isNotFound());
    }

    @Test
    public void deleteReservation_ReservationDoesNotExist_ReturnsNotFound() throws Exception{
        mockMvc.perform(delete("/api/v1/reservations/" + 0))
        .andExpect(status().isNotFound());
    }
}

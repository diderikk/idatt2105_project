package idatt2105.backend.Web;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.RoomDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RoomControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Room room1;
    Room room2;
    Section section1;
    Section section2;
    Reservation reservation1;
    Reservation reservation2;

    @BeforeEach
    public void setup(){
        room1 = new Room();
        room1.setRoomCode("A1");
        room2 = new Room();
        room2.setRoomCode("A2");

        roomRepository.save(room1);
        roomRepository.save(room2);

        reservation1 = new Reservation();
        reservation1.setAmountOfPeople(5);
        reservation1.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        reservation1.setEndTime(LocalDateTime.of(2000, 1, 1, 2, 1, 1));
        reservation1.setReservationText("reservation1");

        reservation2 = new Reservation();
        reservation2.setAmountOfPeople(10);
        reservation2.setStartTime(LocalDateTime.of(2000, 2, 2, 2, 2, 2));
        reservation2.setEndTime(LocalDateTime.of(2000, 2, 2, 3, 2, 2));
        reservation2.setReservationText("reservation2");

        section1 = new Section();
        section1.setRoom(room1);

        section2 = new Section();
        section2.setRoom(room1);

        sectionRepository.save(section1);
        sectionRepository.save(section2);

        reservation1.setSections(List.of(section1));
        reservation2.setSections(List.of(section1));

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        room1.setSections(List.of(section1, section2));

        roomRepository.save(room1);
        roomRepository.save(room2);
    }

    @AfterEach
    public void teardown() {
        reservationRepository.deleteAll();
        sectionRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void getRooms_ReturnAllRoomsAndStatus_StatusOk() throws Exception {
        this.mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.[0].roomCode", is(room1.getRoomCode())))
                .andExpect(jsonPath("$.[1].roomCode", is(room2.getRoomCode())))
                .andReturn();
    }

    @Test
    public void getRoom_ReturnRoomAndStatus_StatusOk() throws Exception {
        this.mockMvc.perform(get("/api/v1/rooms/" + room1.getRoomCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode", is(room1.getRoomCode())))
                .andReturn();
    }

    @Test
    public void createRoom_ReturnCreatedRoomAndStatus_StatusOk() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setRoomCode("roomCode");
        roomDTO.setSections(List.of());
        String roomJson = objectMapper.writeValueAsString(roomDTO);

        this.mockMvc.perform(post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomCode", is(roomDTO.getRoomCode())))
                .andReturn();
    }

    @Test
    public void getReservationsOfRoom_ReturnsListOfReservationsAndStatus_StatusOk() throws Exception {
        this.mockMvc.perform(get("/api/v1/rooms/" + room1.getRoomCode() + "/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].reservationId", is((int)(reservation1.getReservationId()))))
                .andExpect(jsonPath("$.[0].amountOfPeople", is(reservation1.getAmountOfPeople())))
                .andExpect(jsonPath("$.[0].startTime", is(reservation1.getStartTime().toString())))
                .andExpect(jsonPath("$.[0].endTime", is(reservation1.getEndTime().toString())))
                .andExpect(jsonPath("$.[0].reservationText", is(reservation1.getReservationText())))
                .andExpect(jsonPath("$.[1].reservationId", is((int)(reservation2.getReservationId()))))
                .andExpect(jsonPath("$.[1].amountOfPeople", is(reservation2.getAmountOfPeople())))
                .andExpect(jsonPath("$.[1].startTime", is(reservation2.getStartTime().toString())))
                .andExpect(jsonPath("$.[1].endTime", is(reservation2.getEndTime().toString())))
                .andExpect(jsonPath("$.[1].reservationText", is(reservation2.getReservationText())))
                .andReturn();

        this.mockMvc.perform(get("/api/v1/rooms/" + room2.getRoomCode() + "/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }

    @Test
    public void getReservationsOfSection_ReturnsListOfReservationsAndStatus_StatusOk() throws Exception {
        this.mockMvc.perform(get("/api/v1/rooms/" + room1.getRoomCode() + "/sections/" + section1.getSectionId() + "/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].reservationId", is((int)(reservation1.getReservationId()))))
                .andExpect(jsonPath("$.[0].amountOfPeople", is(reservation1.getAmountOfPeople())))
                .andExpect(jsonPath("$.[0].startTime", is(reservation1.getStartTime().toString())))
                .andExpect(jsonPath("$.[0].endTime", is(reservation1.getEndTime().toString())))
                .andExpect(jsonPath("$.[0].reservationText", is(reservation1.getReservationText())))
                .andExpect(jsonPath("$.[1].reservationId", is((int)(reservation2.getReservationId()))))
                .andExpect(jsonPath("$.[1].amountOfPeople", is(reservation2.getAmountOfPeople())))
                .andExpect(jsonPath("$.[1].startTime", is(reservation2.getStartTime().toString())))
                .andExpect(jsonPath("$.[1].endTime", is(reservation2.getEndTime().toString())))
                .andExpect(jsonPath("$.[1].reservationText", is(reservation2.getReservationText())))
                .andReturn();
    }

    @Test
    public void addSectionToRoom_ReturnRoomAndStatus_StatusOk() throws Exception {
        POSTSectionDTO sectionDTO = new POSTSectionDTO();
        sectionDTO.setRoomCode(room2.getRoomCode());
        sectionDTO.setSectionName("sectionName");
        String sectionJson = objectMapper.writeValueAsString(sectionDTO);

        this.mockMvc.perform(post("/api/v1/rooms/" + room2.getRoomCode() + "/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sectionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode", is(room2.getRoomCode())))
                .andReturn();
    }

    @Test
    public void deleteRoom_ShouldDeleteRoom_StatusOk() throws Exception {
        this.mockMvc.perform(delete("/api/v1/rooms/" + room2.getRoomCode()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deleteSectionOfRoom_ShouldDeleteSection_StatusOk() throws Exception {
        this.mockMvc.perform(delete("/api/v1/rooms/" + room1.getRoomCode() + "/sections/" + section1.getSectionId()))
                .andExpect(status().isOk())
                .andReturn();
    }
}

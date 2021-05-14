package idatt2105.backend.Service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.RoomDTO;
import idatt2105.backend.Model.DTO.SectionDTO;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RoomServiceTest {
    
    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SectionRepository sectionRepository;

    Room room1;
    Room room2;
    Section section1;
    Section section2;
    
    @BeforeEach
    public void setup() {
        room1 = new Room();
        room1.setRoomCode("A1");
        room2 = new Room();
        room2.setRoomCode("A2");

        section1 = new Section();
        section1.setRoom(room1);
        section1.setSectionId(1);

        section2 = new Section();
        section2.setRoom(room1);
        section2.setSectionId(2);

        room1.setSections(List.of(section1, section2));

        Mockito.lenient()
        .when(roomRepository.findById("A1"))
        .thenReturn(java.util.Optional.of(room1));
        Mockito.lenient()
        .when(roomRepository.findById("A2"))
        .thenReturn(java.util.Optional.of(room2));

        Mockito.lenient()
        .when(sectionRepository.findById(section1.getSectionId()))
        .thenReturn(java.util.Optional.of(section1));
        Mockito.lenient()
        .when(sectionRepository.findById(section2.getSectionId()))
        .thenReturn(java.util.Optional.of(section2));

        Mockito.lenient()
        .when(roomRepository.findAll())
        .thenReturn(List.of(room1, room2));
    }

    @Test
    public void getRoom_IdExists_RoomIsCorrect()
    {
        RoomDTO roomDTO = roomService.getRoom(room1.getRoomCode());
        assertNotNull(roomDTO);
        assertThat(roomDTO.getRoomCode()).isEqualTo(room1.getRoomCode());
        assertThat(roomDTO.getSections().get(0).getSectionId()).isEqualTo(room1.getSections().get(0).getSectionId());
        assertThat(roomDTO.getSections().get(1).getSectionId()).isEqualTo(room1.getSections().get(1).getSectionId());
    }

    @Test
    public void getRoom_IdDoesNotExists_ReturnsNull()
    {
        RoomDTO roomDTO = roomService.getRoom("-1");
        assertNull(roomDTO);
    }

    @Test
    public void getRooms_RoomsExists_ReturnsListOfRooms()
    {
        List<RoomDTO> rooms = roomService.getRooms();
        assertNotNull(rooms);
        assertThat(rooms.get(0).getRoomCode()).isEqualTo(room1.getRoomCode());
        assertThat(rooms.get(0).getSections().get(0).getSectionId()).isEqualTo(room1.getSections().get(0).getSectionId());
        assertThat(rooms.get(0).getSections().get(1).getSectionId()).isEqualTo(room1.getSections().get(1).getSectionId());
        assertThat(rooms.get(1).getRoomCode()).isEqualTo(room2.getRoomCode());
    }

    @Test
    public void getSectionOfRoom_SectionExists_ReturnsSection()
    {
        SectionDTO section = roomService.getSectionOfRoom(room1.getRoomCode(), section1.getSectionId());
        assertNotNull(section);
        assertThat(section.getSectionId()).isEqualTo(section1.getSectionId());
        assertThat(section.getRoomCode()).isEqualTo(section1.getRoom().getRoomCode());
    }

    @Test
    public void getSectionOfRoom_RoomNotExist_ReturnsNull()
    {
        SectionDTO section = roomService.getSectionOfRoom("-1", section1.getSectionId());
        assertNull(section);
    }

    @Test
    public void getSectionOfRoom_SectionNotExist_ReturnsNull()
    {
        SectionDTO section = roomService.getSectionOfRoom(room1.getRoomCode(), -1);
        assertNull(section);
    }

    @Test
    public void getSectionOfRoom_SectionNotInRightRoom_ReturnsNull()
    {
        SectionDTO section = roomService.getSectionOfRoom(room2.getRoomCode(), section1.getSectionId());
        assertNull(section);
    }

    @Test
    public void getSectionsOfRoom_RoomExists_ReturnsSections()
    {
        List<SectionDTO> sections = roomService.getSectionsOfRoom(room1.getRoomCode());
        assertNotNull(sections);
        assertThat(sections.get(0).getSectionId()).isEqualTo(section1.getSectionId());
        assertThat(sections.get(0).getRoomCode()).isEqualTo(section1.getRoom().getRoomCode());
        assertThat(sections.get(1).getSectionId()).isEqualTo(section2.getSectionId());
        assertThat(sections.get(1).getRoomCode()).isEqualTo(section2.getRoom().getRoomCode());
    }

    @Test
    public void getSectionsOfRoom_RoomNotExist_ReturnsEmptyList()
    {
        List<SectionDTO> sections = roomService.getSectionsOfRoom("-1");
        assertTrue(sections.isEmpty());
    }

    @Test
    public void getSectionsOfRoom_RoomHasNoSections_ReturnsEmptyList()
    {
        List<SectionDTO> sections = roomService.getSectionsOfRoom(room2.getRoomCode());
        assertTrue(sections.isEmpty());
    }

    @Test
    public void createRoom_UsingRoomId_ReturnsRoom()
    {
        String roomCode = "A3";
        RoomDTO room = roomService.createRoom(roomCode);
        assertNotNull(room);
        assertThat(room.getRoomCode()).isEqualTo(roomCode);
    }

    @Test
    public void createRoom_UsingRoomDTO_ReturnsRoom()
    {
        String roomCode = "A3";
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setRoomCode(roomCode);

        SectionDTO section3 = new SectionDTO();
        section3.setRoomCode(roomCode);
        section3.setSectionId(2);

        SectionDTO section4 = new SectionDTO();
        section4.setRoomCode(roomCode);
        section4.setSectionId(3);

        roomDTO.setSections(List.of(section3, section4));

        RoomDTO room = roomService.createRoom(roomDTO);
        assertNotNull(room);
        assertThat(room.getRoomCode()).isEqualTo(roomCode);
        assertThat(room.getSections().get(0).getSectionId()).isEqualTo(section3.getSectionId());
        assertThat(room.getSections().get(1).getSectionId()).isEqualTo(section4.getSectionId());
        assertThat(room.getSections().get(0).getRoomCode()).isEqualTo(section3.getRoomCode());
        assertThat(room.getSections().get(1).getRoomCode()).isEqualTo(section4.getRoomCode());
    }

    @Test
    public void getReservationsOfRoom_RoomExists_ReturnsListOfReservations()
    {
        String roomCode = "A3";
        RoomDTO room = roomService.createRoom(roomCode);
        assertNotNull(room);
        assertThat(room.getRoomCode()).isEqualTo(roomCode);
    }


}

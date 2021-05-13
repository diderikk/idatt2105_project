package idatt2105.backend.Service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;

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
    
    @BeforeEach
    public void setup() {
        Room room1 = new Room();
        room1.setRoomCode("A1");
        Room room2 = new Room();
        room2.setRoomCode("A2");

        Section section1 = new Section();
        section1.setRoom(room1);
        section1.setSectionId(1);

        Section section2 = new Section();
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

}

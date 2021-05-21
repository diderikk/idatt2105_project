package idatt2105.backend.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;
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

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.Section.SectionStatisticsDTO;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;
import javassist.NotFoundException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SectionServiceTest {
    
    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionRepository sectionRepository;

    Room room1;
    Room room2;
    Section section1;
    Section section2;
    Reservation reservation1;
    Reservation reservation2;
    
    @BeforeEach
    public void setup() {
        room1 = new Room();
        room1.setRoomCode("A1");
        room2 = new Room();
        room2.setRoomCode("A2");

        reservation1 = new Reservation();
        reservation1.setAmountOfPeople(5);
        reservation1.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        reservation1.setEndTime(LocalDateTime.of(2000, 1, 1, 2, 1, 1));
        reservation1.setReservationId(1);
        reservation1.setReservationText("reservation1");

        reservation2 = new Reservation();
        reservation2.setAmountOfPeople(10);
        reservation2.setStartTime(LocalDateTime.of(2000, 2, 2, 2, 2, 2));
        reservation2.setEndTime(LocalDateTime.of(2000, 2, 2, 3, 2, 2));
        reservation2.setReservationId(2);
        reservation2.setReservationText("reservation2");

        section1 = new Section();
        section1.setRoom(room1);
        section1.setSectionId(1);
        section1.setSectionName("Section1");
        section1.setReservations(List.of(reservation1, reservation2));

        section2 = new Section();
        section2.setRoom(room1);
        section2.setSectionId(2);
        section2.setSectionName("Section2");

        room1.setSections(List.of(section1, section2));

        Mockito.lenient()
        .when(sectionRepository.findById(section1.getSectionId()))
        .thenReturn(java.util.Optional.of(section1));
        Mockito.lenient()
        .when(sectionRepository.findById(section2.getSectionId()))
        .thenReturn(java.util.Optional.of(section2));

        Mockito.lenient()
        .when(sectionRepository.getAllReservationIdsOfSection(section1.getSectionId()))
        .thenReturn(List.of(reservation1.getReservationId(), reservation2.getReservationId()));
        Mockito.lenient()
        .when(sectionRepository.getAllReservationIdsOfSection(section2.getSectionId()))
        .thenReturn(null);

        Mockito.lenient()
        .when(sectionRepository.existsById(section1.getSectionId()))
        .thenReturn(true);
        Mockito.lenient()
        .when(sectionRepository.existsById(section2.getSectionId()))
        .thenReturn(true);
    }

    @Test
    public void getReservationsOfSection_SectionExists_ReturnsListOfReservations() throws NotFoundException {
        List<GETReservationDTO> reservations = sectionService.getReservationsOfSection(section1.getSectionId());
        assertNotNull(reservations);
        assertThat(!reservations.isEmpty());
        assertThat(reservations.get(0).getReservationId()).isEqualTo(reservation1.getReservationId());
        assertThat(reservations.get(0).getStartTime()).isEqualTo(reservation1.getStartTime());
        assertThat(reservations.get(0).getEndTime()).isEqualTo(reservation1.getEndTime());
        assertThat(reservations.get(0).getAmountOfPeople()).isEqualTo(reservation1.getAmountOfPeople());
        
        assertThat(reservations.get(1).getReservationId()).isEqualTo(reservation2.getReservationId());
        assertThat(reservations.get(1).getStartTime()).isEqualTo(reservation2.getStartTime());
        assertThat(reservations.get(1).getEndTime()).isEqualTo(reservation2.getEndTime());
        assertThat(reservations.get(1).getAmountOfPeople()).isEqualTo(reservation2.getAmountOfPeople());
    }

    @Test
    public void getReservationsOfSection_SectionDoesNotExists_ThrowsNotFoundException() throws NotFoundException {
        List<GETReservationDTO> reservations = sectionService.getReservationsOfSection(section1.getSectionId());
        assertThrows(NotFoundException.class, () -> sectionService.getReservationsOfSection(-1));
    }

    @Test
    public void getTopSections_FindsAllTopSections_ReturnsListOfSections() {
        int amountReservationsSection1 = (section1.getReservations() == null) ? 0 : section1.getReservations().size();
        int amountReservationsSection2 = (section2.getReservations() == null) ? 0 : section2.getReservations().size();

        if(amountReservationsSection1 > amountReservationsSection2) {
            Mockito.lenient()
            .when(sectionRepository.getTopSections())
            .thenReturn(List.of(section1, section2));
        } else {
            Mockito.lenient()
            .when(sectionRepository.getTopSections())
            .thenReturn(List.of(section2, section1));
        }

        List<GETSectionDTO> sections = sectionService.getTopSections();
        assertNotNull(sections);
        assertThat(sections.get(0).getSectionId()).isEqualTo(section1.getSectionId());
        assertThat(sections.get(1).getSectionId()).isEqualTo(section2.getSectionId());
        assertThat(sections.get(0).getSectionName()).isEqualTo(section1.getSectionName());
        assertThat(sections.get(1).getSectionName()).isEqualTo(section2.getSectionName());
        assertThat(sections.get(0).getRoomCode()).isEqualTo(section1.getRoom().getRoomCode());
        assertThat(sections.get(1).getRoomCode()).isEqualTo(section2.getRoom().getRoomCode());
    }

    @Test
    public void getStatistics_SectionExists_ReturnsSectionStatisticsDTO() throws NotFoundException {
        Long sum = 0L;
        for(Reservation reservation : section1.getReservations()) {
            long hours = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours();
            sum += hours;
        }

        Mockito.lenient()
        .when(sectionRepository.getTotalHoursBooked(section1.getSectionId()))
        .thenReturn(Optional.of(sum));

        SectionStatisticsDTO sectionStatisticsDTO = sectionService.getStatistics(section1.getSectionId());
        assertNotNull(sectionStatisticsDTO);
        assertEquals(sum, sectionStatisticsDTO.getTotalHoursOfReservations());
    }
}

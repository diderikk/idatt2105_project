package idatt2105.backend.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.ReservationDTO;
import idatt2105.backend.Model.DTO.RoomDTO;
import idatt2105.backend.Model.DTO.SectionDTO;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;

@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SectionRepository sectionRepository;

    /**
     * Returns room based on roomCode stored in the database
     * @param roomCode
     * @return RoomDTO object
     */
    public RoomDTO getRoom(String roomCode)
    {
        LOGGER.info("getRoom(String roomCode) called with roomCode: {}", roomCode); 
        Optional<Room> room = roomRepository.findById(roomCode);
        if(room.isPresent()) return new RoomDTO(room.get());
        return null;
    }

    /**
     * Returns all rooms stored in the database
     * @return List of rooms
     */
    public List<RoomDTO> getRooms()
    {
        LOGGER.info("getRooms() called");
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(room -> new RoomDTO(room)).collect(Collectors.toList());
    }

    /**
     * Returns a specific section of a specific room given by roomCode and sectionID
     * @param roomCode
     * @param sectionId
     * @return SectionDTO, or null if room does not exist or section not found
     */
    public SectionDTO getSectionOfRoom(String roomCode, long sectionId)
    {
        LOGGER.info("getSectionOfRoom(String roomCode, long sectionId) called with roomCode: {}, and sectionId: {}", roomCode, sectionId);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(roomOptional.isPresent()) {
            for(Section section : roomOptional.get().getSections()) {
                if(section.getSectionId() == sectionId) return new SectionDTO(section);
            }
        }
        return null;
    }

    /**
     * Returns all sections of a specific room given by roomCode
     * @param roomCode
     * @return List of sections
     */
    public List<SectionDTO> getSectionsOfRoom(String roomCode)
    {
        LOGGER.info("getSectionsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(roomOptional.isPresent()) return roomOptional.get().getSections().stream().map(section -> new SectionDTO(section)).collect(Collectors.toList());
        return new ArrayList<>();
    }

    /**
     * Creates a new room with a given roomCode
     * Sections are empty.
     * @param roomCode
     * @return RoomDTO object
     */
    public RoomDTO createRoom(String roomCode)
    {
        LOGGER.info("createRoomWith(String roomCode) called with roomCode: {}", roomCode);

        // TODO: add ADMIN verification?

        Room newRoom = new Room();
        newRoom.setRoomCode(roomCode);
        roomRepository.save(newRoom);
        return new RoomDTO(newRoom);
    }

    /**
     * Creates a new room from a given roomDTO
     * @param roomDTO
     * @return RoomDTO object
     */
    public RoomDTO createRoom(RoomDTO roomDTO)
    {
        LOGGER.info("createRoom(RoomDTO roomDTO) called with roomCode: {}", roomDTO.getRoomCode());

        // TODO: add ADMIN verification?

        Room newRoom = new Room();
        newRoom.setRoomCode(roomDTO.getRoomCode());

        List<SectionDTO> sectionDTOs = roomDTO.getSections();
        List<Section> sections = sectionDTOs.stream().map(sectionDTO -> {
            Section section = new Section();
            section.setSectionId(sectionDTO.getSectionId());
            section.setRoom(newRoom);
            return section;
        }).collect(Collectors.toList());
        newRoom.setSections(sections);

        roomRepository.save(newRoom);
        return new RoomDTO(newRoom);
    }

    /**
     * Returns list of all resevations of a room, given by roomCode
     * @param roomCode
     * @return List of reservationDTOs
     */
    public List<ReservationDTO> getReservationsOfRoom(String roomCode)
    {
        LOGGER.info("getReservationsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> room = roomRepository.findById(roomCode);
        
        //Return null if no room is present
        if(!room.isPresent()) return null;

        List<Section> sections = room.get().getSections();
        List<Reservation> reservations = new ArrayList<>();
        for(Section section : sections) {
            reservations.addAll(section.getReservations());
        }
        return reservations.stream().map(reservation -> new ReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Returns list of all resevations of a specific rooms section, given by roomCode and sectionId
     * @param roomCode
     * @param sectionDTO
     * @return List of reservationDTOs, null if no section or section is not of the specific room
     */
    public List<ReservationDTO> getReservationsOfSection(String roomCode, long sectionId)
    {
        LOGGER.info("getReservationsOfSection(String roomCode, long sectionId) called with roomCode: {}, and sectionID: {}", roomCode, sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);

        //Return null if no section is present
        if(!sectionOptional.isPresent()) return null;

        List<ReservationDTO> reservations = sectionOptional.get().getReservations().stream().map(reservation -> new ReservationDTO(reservation)).collect(Collectors.toList());

        // Should return if section is in the right room
        if(sectionOptional.get().getRoom() != null && sectionOptional.get().getRoom().getRoomCode().equals(roomCode)) return reservations;
        return null;
    }

    /**
     * Adds a section to a room, given by roomCode and sectionDTO
     * New section is also saved in the database
     * @param roomCode
     * @param sectionDTO
     * @return RoomDTO object
     */
    public RoomDTO addSectionToRoom(String roomCode, SectionDTO sectionDTO)
    {
        LOGGER.info("addSectionToRoom(String roomCode, SectionDTO sectionDTO) called with roomCode: {}", roomCode);

        // TODO: add ADMIN verification?

        Optional<Room> room = roomRepository.findById(roomCode);
        if(!room.isPresent()) return null;

        Section newSection = new Section();
        newSection.setRoom(room.get());
        List<Section> sections = room.get().getSections();
        sections.add(newSection);
        room.get().setSections(sections);
        sectionRepository.save(newSection);
        return new RoomDTO(roomRepository.save(room.get()));
    }
}
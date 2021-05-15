package idatt2105.backend.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import idatt2105.backend.Exception.NoRoomFoundException;
import idatt2105.backend.Exception.NoSectionFoundException;
import idatt2105.backend.Exception.SectionsListOfRoomIsNullException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.GETSectionDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.RoomDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;

@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Returns room based on roomCode stored in the database
     * @param roomCode
     * @return RoomDTO object
     * @throws NoRoomFoundException
     */
    public RoomDTO getRoom(String roomCode) throws NoRoomFoundException
    {
        LOGGER.info("getRoom(String roomCode) called with roomCode: {}", roomCode); 
        Optional<Room> room = roomRepository.findById(roomCode);
        if(!room.isPresent()){
            throw new NoRoomFoundException("No room found with room code: " + roomCode);
        }
        return new RoomDTO(room.get());
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
     * @throws NoRoomFoundException
     * @throws NoSectionFoundException
     */
    public GETSectionDTO getSectionOfRoom(String roomCode, long sectionId) throws NoRoomFoundException, NoSectionFoundException
    {
        LOGGER.info("getSectionOfRoom(String roomCode, long sectionId) called with roomCode: {}, and sectionId: {}", roomCode, sectionId);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(!roomOptional.isPresent()) {
            throw new NoRoomFoundException("No room found with room code: " + roomCode);
        }
        if(roomOptional.get().getSections() == null) {
            throw new SectionsListOfRoomIsNullException("Sections list of room with code '" + roomCode + "' is null");
        }

        for(Section section : roomOptional.get().getSections()) {
            if(section.getSectionId() == sectionId) return new GETSectionDTO(section);
        }
        throw new NoSectionFoundException("No section found with id: " + sectionId);
    }

    /**
     * Returns all sections of a specific room given by roomCode
     * @param roomCode
     * @return List of sections
     * @throws NoRoomFoundException
     */
    public List<GETSectionDTO> getSectionsOfRoom(String roomCode) throws NoRoomFoundException
    {
        LOGGER.info("getSectionsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(!roomOptional.isPresent()) {
            throw new NoRoomFoundException("No room found with room code: " + roomCode);
        }
        if(roomOptional.get().getSections() == null) {
            throw new SectionsListOfRoomIsNullException("Sections list of room with code '" + roomCode + "' is null");
        }
        return roomOptional.get().getSections().stream().map(section -> new GETSectionDTO(section)).collect(Collectors.toList());
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

        List<GETSectionDTO> sectionDTOs = roomDTO.getSections();
        List<Section> sections = new ArrayList<>();
        for(GETSectionDTO sectionDTO : sectionDTOs) {
            Section section = new Section();
            section.setSectionId(sectionDTO.getSectionId());
            section.setRoom(newRoom);
            section.setSectionName(sectionDTO.getSectionName());
            sections.add(section);
        }
        newRoom.setSections(sections);

        sectionRepository.saveAll(sections);
        roomRepository.save(newRoom);
        return new RoomDTO(newRoom);
    }

    /**
     * Returns list of all resevations of a room, given by roomCode
     * @param roomCode
     * @return List of reservationDTOs
     * @throws NoRoomFoundException
     */
    public List<GETReservationDTO> getReservationsOfRoom(String roomCode) throws NoRoomFoundException
    {
        LOGGER.info("getReservationsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        
        if(!roomOptional.isPresent()) {
            throw new NoRoomFoundException("No room found with room code: " + roomCode);
        }

        List<Section> sections = roomOptional.get().getSections();
        if(sections == null) sections = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();
        for(Section section : sections) {
            if(section.getReservations() != null) reservations.addAll(section.getReservations());
        }
        return reservations.stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Returns list of all resevations of a specific rooms section, given by roomCode and sectionId
     * @param roomCode
     * @param sectionDTO
     * @return List of reservationDTOs, null if no section or section is not of the specific room
     * @throws NoSectionFoundException
     */
    public List<GETReservationDTO> getReservationsOfSection(String roomCode, long sectionId) throws NoSectionFoundException
    {
        LOGGER.info("getReservationsOfSection(String roomCode, long sectionId) called with roomCode: {}, and sectionID: {}", roomCode, sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);

        if(!sectionOptional.isPresent()) {
            throw new NoSectionFoundException("No section found with id: " + sectionId);
        }

        List<GETReservationDTO> reservations = sectionOptional.get().getReservations().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());

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
    @Transactional
    public RoomDTO addSectionToRoom(POSTSectionDTO sectionDTO)
    {
        LOGGER.info("addSectionToRoom(SectionDTO sectionDTO) called with roomCode: {}", sectionDTO.getRoomCode());

        // TODO: add ADMIN verification?

        Optional<Room> optionalRoom = roomRepository.findById(sectionDTO.getRoomCode());
        if(!optionalRoom.isPresent() || isSectionNameInRoom(optionalRoom.get(), sectionDTO.getSectionName())) return null;
        Room room = optionalRoom.get();

        Section newSection = new Section();
        newSection.setRoom(room);
        newSection.setSectionName(sectionDTO.getSectionName());
        List<Section> sections = room.getSections();
        if(sections == null) sections = new ArrayList<>();
        sections.add(newSection);
        room.setSections(sections);
        sectionRepository.save(newSection);
        return new RoomDTO(room);
    }

    /**
     * Finds room based on roomCode and deletes it
     * @param roomCode
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteRoom(String roomCode)
    {
        LOGGER.info("deleteRoom(String roomCode) called with roomCode: {}", roomCode);

        Optional<Room> room = roomRepository.findById(roomCode);
        if(room.isPresent()) {
            if(room.get().getSections() != null) {
                for(Section section : room.get().getSections()) {
                    Optional<List<Long>> reservationIds = sectionRepository.getAllReservationIdsOfSection(section.getSectionId());
                    if(reservationIds.isPresent()) {
                        reservationRepository.deleteGivenReservations(reservationIds.get());
                    }
                    sectionRepository.delete(section);
                }
            }
            roomRepository.deleteById(roomCode);
        }
        return !roomRepository.existsById(roomCode);
    }

    /**
     * Finds section based on id and deletes it
     * @param roomCode
     * @param sectionId
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteSectionOfRoom(String roomCode, long sectionId)
    {
        LOGGER.info("deleteSectionOfRoom(String roomCode, long sectionId) called with roomCode: {}, and sectionId: {}", roomCode, sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);

        // Return false if no section or room is present
        if(!sectionOptional.isPresent()) return false;
        if(!roomOptional.isPresent()) return false;

        // Delete only if section is of this room
        if(sectionOptional.get().getRoom() != null && sectionOptional.get().getRoom().getRoomCode().equals(roomCode)) {
            // Delete reservations first
            Optional<List<Long>> reservationIds = sectionRepository.getAllReservationIdsOfSection(sectionId);
            if(reservationIds.isPresent()) {
                reservationRepository.deleteGivenReservations(reservationIds.get());
            }
            sectionRepository.deleteById(sectionId);
            return !sectionRepository.existsById(sectionId);
        }
        return false;
    }

    private boolean isSectionNameInRoom(Room room, String sectionName){
        if(room.getSections() == null) return false;
        for(Section section : room.getSections()) if(section.getSectionName().equals(sectionName)) return true;
        return false;
    }
}
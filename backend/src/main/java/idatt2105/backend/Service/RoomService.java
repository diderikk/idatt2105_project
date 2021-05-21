package idatt2105.backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import idatt2105.backend.Exception.AlreadyExistsException;
import idatt2105.backend.Exception.SectionNameInRoomAlreadyExistsException;
import idatt2105.backend.Exception.SectionNotOfThisRoomException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.DTO.TimeIntervalDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import idatt2105.backend.Model.DTO.Room.POSTRoomDTO;
import idatt2105.backend.Model.DTO.Room.RoomStatisticsDTO;
import idatt2105.backend.Model.DTO.Section.AvailableSectionDTO;
import idatt2105.backend.Model.DTO.Section.AvailableSectionsDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;
import javassist.NotFoundException;

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
     * @throws NotFoundException
     */
    public GETRoomDTO getRoom(String roomCode) throws NotFoundException
    {
        LOGGER.info("getRoom(String roomCode) called with roomCode: {}", roomCode); 
        Optional<Room> room = roomRepository.findById(roomCode);
        if(!room.isPresent()){
            throw new NotFoundException("No room found with room code: " + roomCode);
        }
        return new GETRoomDTO(room.get());
    }

    /**
     * Returns all rooms stored in the database
     * @return List of rooms
     */
    public List<GETRoomDTO> getRooms()
    {
        LOGGER.info("getRooms() called");
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(room -> new GETRoomDTO(room)).collect(Collectors.toList());
    }

    /**
     * Finds room that are available
     * @param startTime
     * @param endTime
     * @return List of rooms that have available sections between start time and end time
     */
    public AvailableSectionsDTO getAvailableRooms(TimeIntervalDTO dto){
        List<AvailableSectionDTO> availableSections = sectionRepository.getAvailableSections(dto.getStartTime(), dto.getEndTime())
        .stream().map(section -> new AvailableSectionDTO(section.getSectionId())).collect(Collectors.toList());
        return new AvailableSectionsDTO(getRooms(), availableSections);
    }

    /**
     * Finds room that are available with input start time and end time
     * @param startTime
     * @param endTime
     * @return List of rooms that have available sections between start time and end time
     */
    public AvailableSectionsDTO getAvailableRooms(TimeIntervalDTO dto, long reservationId){
        List<AvailableSectionDTO> availableSections = sectionRepository.getAvailableSections(dto.getStartTime(), dto.getEndTime(), reservationId)
        .stream().map(section -> new AvailableSectionDTO(section.getSectionId())).collect(Collectors.toList());
        return new AvailableSectionsDTO(getRooms(), availableSections);

    }

    /**
     * Returns a specific section of a specific room given by roomCode and sectionID
     * @param roomCode
     * @param sectionId
     * @return SectionDTO
     * @throws NotFoundException
     * @throws NullPointerException
     */
    public GETSectionDTO getSectionOfRoom(String roomCode, long sectionId) throws NotFoundException
    {
        LOGGER.info("getSectionOfRoom(String roomCode, long sectionId) called with roomCode: {}, and sectionId: {}", roomCode, sectionId);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(!roomOptional.isPresent()) {
            throw new NotFoundException("No room found with room code: " + roomCode);
        }

        for(Section section : roomOptional.get().getSections()) {
            if(section.getSectionId() == sectionId) return new GETSectionDTO(section);
        }
        throw new NotFoundException("No section found with id: " + sectionId);
    }

    /**
     * Returns all sections of a specific room given by roomCode
     * @param roomCode
     * @return List of sections
     * @throws NotFoundException
     * @throws NullPointerException
     */
    public List<GETSectionDTO> getSectionsOfRoom(String roomCode) throws NotFoundException
    {
        LOGGER.info("getSectionsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        if(!roomOptional.isPresent()) {
            throw new NotFoundException("No room found with room code: " + roomCode);
        }
        if(roomOptional.get().getSections() == null) {
            throw new NullPointerException("Sections list of room with code '" + roomCode + "' is null");
        }
        return roomOptional.get().getSections().stream().map(section -> new GETSectionDTO(section)).collect(Collectors.toList());
    }

    /**
     * Creates a new room with a given roomCode
     * Sections are empty.
     * @param roomCode
     * @return RoomDTO object
     */
    public GETRoomDTO createRoom(String roomCode)
    {
        LOGGER.info("createRoomWith(String roomCode) called with roomCode: {}", roomCode);

        // TODO: add ADMIN verification?

        Room newRoom = new Room();
        newRoom.setRoomCode(roomCode);
        roomRepository.save(newRoom);
        return new GETRoomDTO(newRoom);
    }

    /**
     * Creates a new room from a given roomDTO
     * @param roomDTO
     * @return RoomDTO object
     * @throws AlreadyExistsException
     */
    public GETRoomDTO createRoom(POSTRoomDTO roomDTO) throws AlreadyExistsException
    {
        LOGGER.info("createRoom(RoomDTO roomDTO) called with roomCode: {}", roomDTO.getRoomCode());

        // TODO: add ADMIN verification?
        if(roomRepository.findById(roomDTO.getRoomCode()).isPresent()) throw new AlreadyExistsException("Room code already exists");

        Room newRoom = new Room();
        newRoom.setRoomCode(roomDTO.getRoomCode());
        newRoom = roomRepository.save(newRoom);

        List<POSTSectionDTO> sectionDTOs = roomDTO.getSections();
        List<Section> sections = new ArrayList<>();
        for(POSTSectionDTO sectionDTO : sectionDTOs) {
            Section section = new Section();
            section.setRoom(newRoom);
            section.setSectionName(sectionDTO.getSectionName());
            sections.add(section);
        }
        newRoom.setSections(sections);

        sectionRepository.saveAll(sections);
        return new GETRoomDTO(newRoom);
    }

    /**
     * Edits a room, given by roomCode and information from POSTRoomDTO
     * @param roomCode
     * @param roomDTO
     * @return GETRoomDTO of the edited toom
     * @throws AlreadyExistsException if room with same code already exists
     * @throws NotFoundException if no room with the given roomCode was found
     */
    @Transactional
    public GETRoomDTO editRoom(String roomCode, POSTRoomDTO roomDTO) throws AlreadyExistsException, NotFoundException{
        LOGGER.info("editRoom(String roomCode, POSTRoomDTO roomDTO) called with roomCode: {}", roomCode);
        Optional<Room> optionalRoom = roomRepository.findById(roomCode);
        if(!optionalRoom.isPresent()) throw new NotFoundException("Room with room code: " + roomDTO.getRoomCode() + " not found");
        if(roomRepository.findById(roomDTO.getRoomCode()).isPresent() && !roomCode.equals(roomDTO.getRoomCode())) throw new AlreadyExistsException("Room with room code: " + roomCode + " already exists");

        if(roomDTO.getRoomCode() != null && roomDTO.getSections() != null && !roomDTO.getSections().isEmpty()){
            deleteRoom(roomCode);
            return createRoom(roomDTO);
        }
        throw new IllegalArgumentException("Either list was empty or null, or roomCode was null");
    }

    /**
     * Returns list of all resevations of a room, given by roomCode
     * @param roomCode
     * @return List of GETReservationDTOs
     * @throws NotFoundException
     */
    public List<GETReservationDTO> getReservationsOfRoom(String roomCode) throws NotFoundException
    {
        LOGGER.info("getReservationsOfRoom(String roomCode) called with roomCode: {}", roomCode);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        
        if(!roomOptional.isPresent()) {
            throw new NotFoundException("No room found with room code: " + roomCode);
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
     * @return List of GETReservationDTOs
     * @throws NotFoundException
     * @throws NullPointerException
     * @throws SectionNotOfThisRoomException
     */
    public List<GETReservationDTO> getReservationsOfSection(String roomCode, long sectionId) throws NotFoundException, SectionNotOfThisRoomException
    {
        LOGGER.info("getReservationsOfSection(String roomCode, long sectionId) called with roomCode: {}, and sectionID: {}", roomCode, sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);

        if(!sectionOptional.isPresent()) {
            throw new NotFoundException("No section found with id: " + sectionId);
        }

        List<GETReservationDTO> reservations = sectionOptional.get().getReservations().stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());

        if(sectionOptional.get().getRoom() == null) {
            throw new NullPointerException("Section with id " + sectionId + " has no room (room == null)");
        }

        // Should return if section is in the right room
        if(sectionOptional.get().getRoom().getRoomCode().equals(roomCode)) return reservations;
        throw new SectionNotOfThisRoomException("Sections roomCode does not match given roomCode");
    }

    /**
     * Adds a section to a room, given by roomCode and sectionDTO
     * New section is also saved in the database
     * @param roomCode
     * @param sectionDTO
     * @return RoomDTO object
     * @throws NotFoundException
     * @throws SectionNameInRoomAlreadyExistsException
     */
    @Transactional
    public GETRoomDTO addSectionToRoom(POSTSectionDTO sectionDTO) throws NotFoundException, SectionNameInRoomAlreadyExistsException
    {
        LOGGER.info("addSectionToRoom(SectionDTO sectionDTO) called with roomCode: {}", sectionDTO.getRoomCode());

        // TODO: add ADMIN verification?

        Optional<Room> roomOptional = roomRepository.findById(sectionDTO.getRoomCode());
        if(!roomOptional.isPresent()) {
            throw new NotFoundException("No room found with room code: " + sectionDTO.getRoomCode());
        }
        if(isSectionNameInRoom(roomOptional.get(), sectionDTO.getSectionName())) {
            throw new SectionNameInRoomAlreadyExistsException("Can not add section to room. Section with name '" + sectionDTO.getSectionName() + "' already exists in the room");
        }
        Room room = roomOptional.get();

        Section newSection = new Section();
        newSection.setRoom(room);
        newSection.setSectionName(sectionDTO.getSectionName());
        List<Section> sections = room.getSections();
        if(sections == null) sections = new ArrayList<>();
        sections.add(newSection);
        room.setSections(sections);
        sectionRepository.save(newSection);
        return new GETRoomDTO(room);
    }

    /**
     * Finds room based on roomCode and deletes it
     * @param roomCode
     * @return true if deletion was successful, false otherwise
     * @throws NotFoundException
     */
    public boolean deleteRoom(String roomCode) throws NotFoundException
    {
        LOGGER.info("deleteRoom(String roomCode) called with roomCode: {}", roomCode);

        Optional<Room> room = roomRepository.findById(roomCode);
        if(!room.isPresent()) {
            throw new NotFoundException("No room found with room code: " + roomCode);
        }

        // Delete all sections and reservations of this room, if there are any
        if(room.get().getSections() != null) {
            for(Section section : room.get().getSections()) {
                List<Long> reservationIds = sectionRepository.getAllReservationIdsOfSection(section.getSectionId());
                if(reservationIds != null) {
                    reservationRepository.deleteGivenReservationsDependencies(reservationIds);
                    reservationRepository.deleteGivenReservations(reservationIds);
                }
                sectionRepository.delete(section);
            }
        }
        roomRepository.deleteById(roomCode);
        return !roomRepository.existsById(roomCode);
    }

    /**
     * Finds section based on id and deletes it
     * @param roomCode
     * @param sectionId
     * @return true if deletion was successful, false otherwise
     * @throws NotFoundException
     * @throws SectionNotOfThisRoomException
     * @throws NullPointerException
     */
    public boolean deleteSectionOfRoom(String roomCode, long sectionId) throws NotFoundException, SectionNotOfThisRoomException
    {
        LOGGER.info("deleteSectionOfRoom(String roomCode, long sectionId) called with roomCode: {}, and sectionId: {}", roomCode, sectionId);
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        Optional<Room> roomOptional = roomRepository.findById(roomCode);
        // Throw exceptions if no section or room is present, or if section has room == null
        if(!sectionOptional.isPresent()) throw new NotFoundException("No section found with id: " + sectionId);
        if(!roomOptional.isPresent()) throw new NotFoundException("No room found with room code: " + roomCode);
        if(sectionOptional.get().getRoom() == null) throw new NullPointerException("Section with id " + sectionId + " has no room (room == null)");
        // Check if section is of this room
        if(!sectionOptional.get().getRoom().getRoomCode().equals(roomCode)) throw new SectionNotOfThisRoomException("Section with id " + sectionId + " is not of room with code " + roomCode);

        // Delete all reservations of the section, if there are any
        List<Long> reservationIds = sectionRepository.getAllReservationIdsOfSection(sectionId);
        if(reservationIds != null) {
            reservationRepository.deleteGivenReservationsDependencies(reservationIds);
            reservationRepository.deleteGivenReservations(reservationIds);
        }
        sectionRepository.deleteById(sectionId);
        return !sectionRepository.existsById(sectionId);
    }

    /**
     * Finds top 5 most popular rooms.
     * @return List of rooms, empty list if no rooms were found
     */
    public List<GETRoomDTO> getTopRooms() {
        LOGGER.info("getTopRooms() was called");
        List<Room> rooms = roomRepository.getTopRooms();
        List<GETRoomDTO> roomDTOs = new ArrayList<>();
        for(Room room : rooms) {
            roomDTOs.add(new GETRoomDTO(room));
        }
        return roomDTOs;
    }

    /**
     * Get statistics about a room, given by room code.
     * Statistics such as total hours room has been booked/reserved.
     * @param roomCode
     * @return RoomStatisticsDTO
     * @throws NotFoundException
     */
    public RoomStatisticsDTO getStatistics(String roomCode) throws NotFoundException {
        LOGGER.info("getStatistics(String roomCode) called with roomCode: {}", roomCode);
        if(!roomRepository.existsById(roomCode)) {
            LOGGER.warn("Could not find room with code: {}. Throwing exception", roomCode);
            throw new NotFoundException("No room found with code: " + roomCode);
        }

        Optional<Long> totalHoursBookedOptional = roomRepository.getTotalHoursBooked(roomCode);
        Long totalHoursBooked = 0L;
        if(totalHoursBookedOptional.isPresent()) {
            totalHoursBooked = totalHoursBookedOptional.get();
        }
        
        return new RoomStatisticsDTO(totalHoursBooked);
    }

    /**
     * Private method used to check if section with specified name exists in room
     * @param room
     * @param sectionName
     * @return true if section is in room, false otherwise
     */
    private boolean isSectionNameInRoom(Room room, String sectionName){
        if(room.getSections() == null) return false;
        for(Section section : room.getSections()) if(section.getSectionName().equals(sectionName)) return true;
        return false;
    }
}
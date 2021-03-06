package idatt2105.backend.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import idatt2105.backend.Component.EmailComponent;
import idatt2105.backend.Exception.AlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.Message;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Room;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Reservation.POSTReservationDTO;
import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import idatt2105.backend.Model.DTO.User.GETUserDTO;
import idatt2105.backend.Model.DTO.User.POSTUserDTO;
import idatt2105.backend.Model.DTO.User.UserStatisticsDTO;
import idatt2105.backend.Model.Enum.SortingTypeEnum;
import idatt2105.backend.Repository.MessageRepository;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.RoomRepository;
import idatt2105.backend.Repository.SectionRepository;
import idatt2105.backend.Repository.UserRepository;
import javassist.NotFoundException;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired(required = false)
    private EmailComponent emailComponent;

    /**
     * Returns user from database based on given userId
     * @param userId
     * @return UserDTO
     * @throws NotFoundException if iser not found
     */
    public GETUserDTO getUser(long userId) throws NotFoundException {
        LOGGER.info("getUser(long userId) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("No user found with id: " + userId);
        }
        User user = optionalUser.get();
        return new GETUserDTO(user);
    }

    public List<GETUserDTO> getUsers(){
        LOGGER.info("getUsers() was called");
        return userRepository.findAll().stream().map(user -> new GETUserDTO(user)).collect(Collectors.toList());
    }

    /**
     * Creates user based on given information from UserDTO and stores it in database.
     * Also checks if email, first name and last name fields in given DTO are not null.
     * Also checks if email is already existant. Emails have to be unique for every user.
     * @param inputUser
     * @return UserDTO that was created
     * @throws NullPointerException if some of the fields in dto are null
     * @throws EmailAlreadyExistsException if user with this email already exists
     */
    public GETUserDTO createUser(POSTUserDTO inputUser) throws AlreadyExistsException, NullPointerException {
        LOGGER.info("createUser(POSTUserDTO inputUser) was called with inputUser's email: {}", inputUser.getEmail());
        if (inputUser.getEmail() == null || inputUser.getFirstName() == null || inputUser.getLastName() == null) {
            throw new NullPointerException("InputUserDTO object has some fields that are null");
        }
        // Check if email alrady exists
        if(userRepository.findUserByEmail(inputUser.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email " + inputUser.getEmail() + " already exists");
        }
        String randomPassword = createRandomPassword();
        User createdUser = new User();
        createdUser.setFirstName(inputUser.getFirstName());
        createdUser.setLastName(inputUser.getLastName());
        createdUser.setEmail(inputUser.getEmail());
        createdUser.setPhoneNumber(inputUser.getPhoneNumber());
        createdUser.setExpirationDate(inputUser.getExpirationDate());
        createdUser.setAdmin(inputUser.isAdmin());
        createdUser.setHash(passwordEncoder.encode(randomPassword));
        if(emailComponent != null) emailComponent.sendPassword(createdUser.getEmail(), randomPassword);
        createdUser = userRepository.save(createdUser);

        return new GETUserDTO(createdUser);
    }

    /**
     * Edits a given user if fields are not null.
     * Checks if email already exists before allowing the edit user;
     * @param userId
     * @param inputUser
     * @return
     * @throws NotFoundException
     * @throws AlreadyExistsException
     */
    public GETUserDTO editUser(long userId, POSTUserDTO inputUser) throws NotFoundException, AlreadyExistsException{
        LOGGER.info("editUser(long userId, POSTUserDTO inputUser) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<User> tempUser = userRepository.findUserByEmail(inputUser.getEmail());
        if(tempUser.isPresent() && tempUser.get().getUserId() != userId) 
            throw new AlreadyExistsException("Email " + inputUser.getEmail() + " already exists");
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(!inputUser.getEmail().isEmpty() || inputUser.getEmail() != null) user.setEmail(inputUser.getEmail());
            if(inputUser.getFirstName() != null) user.setFirstName(inputUser.getFirstName());
            if(inputUser.getLastName() != null) user.setLastName(inputUser.getLastName());
            if(inputUser.getExpirationDate() != null) user.setExpirationDate(inputUser.getExpirationDate());
            if(inputUser.getPhoneNumber() != null) user.setPhoneNumber(inputUser.getPhoneNumber());
            user.setAdmin(inputUser.isAdmin());
            user = userRepository.save(user);
            
            return new GETUserDTO(user);
        }
        throw new NotFoundException("No user found with id: " + userId);
    } 

    /**
     * Changes password of a user, given by userId in the ChangePasswordDTO parameter.
     * @param dto
     * @return true if user changed password successfully
     * @throws NotFoundException if user not found
     * @throws ValidationException if old password didn't match
     */
    public boolean changePassword(ChangePasswordDTO dto) throws ValidationException, NotFoundException {
        LOGGER.info("changePassword(ChangePasswordDTO dto) was called with dto userId: {}", dto.getUserId());
        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + dto.getUserId());
        if(!passwordEncoder.matches(dto.getOldPassword(), optionalUser.get().getHash())) {
            throw new ValidationException("Old password didn't match");
        }
        User user = optionalUser.get();
        user.setHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    /**
     * Finds and returns all reservations that this user has made
     * @param userId
     * @return List of GETReservationDTOs
     * @throws NotFoundException if user not found
     */
    public List<GETReservationDTO> getUserReservations(long userId) throws NotFoundException {
        LOGGER.info("getUserReservations(long userId) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        return user.getReservations().stream().filter(reservation -> reservation.getStartTime().isAfter(LocalDateTime.now())).map(reservation -> new GETReservationDTO(reservation))
                .collect(Collectors.toList());
    }

    /**
     * Gets one userReservation if it exists in their reservations
     * @param userId
     * @param reservationId
     * @return
     * @throws NotFoundException
     */
    public GETReservationDTO getUserReservation(long userId, long reservationId) throws NotFoundException {
        LOGGER.info("getUserReservations(long userId) was called with userId: {}, reservationId: {}", userId, reservationId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        Optional<Reservation> optionalReservation = optionalUser.get().getReservations().stream().filter(reservation -> reservation.getReservationId() == reservationId).findAny();
        if(optionalReservation.isPresent()) return new GETReservationDTO(optionalReservation.get());
        throw new NotFoundException("No reservation found with id: " + reservationId);
    }

    /**
     * Finds and returns all filtered reservations sorted, that this user has made;
     * @param userId
     * @param dto
     * @return List of reservations
     */
    public List<GETReservationDTO> getSortedAndFilteredReservations(long userId, SortingDTO dto){
        List<Reservation> reservations;
        String roomQuery = "%" + dto.getRoomQuery() + "%";

        if(dto.getSortType() == SortingTypeEnum.AMOUNT_OF_PEOPLE) 
            reservations = reservationRepository.getUserFutureReservationSortedByPeople(roomQuery, userId);
        else
            reservations = reservationRepository.getUserFutureReservationSortedByDate(roomQuery, userId);
        
        return reservations.stream().map(reservation -> new GETReservationDTO(reservation)).collect(Collectors.toList());
    }

    /**
     * Creates a new reservation based on the information 
     * in given POSTReservationDTO, and adds it to the user.
     * @param userId
     * @param dto
     * @return POSTReservationDTO of the reservation that was added
     * @throws NotFoundException if user not found
     */
    public POSTReservationDTO addUserReservation(long userId, POSTReservationDTO dto) throws NotFoundException, SectionAlreadyBookedException {
        LOGGER.info("addUserReservation(long userId, POSTReservationDTO dto) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        Reservation reservation = new Reservation();
        if(dto.getStartTime() != null && dto.getStartTime().isBefore(dto.getEndTime())) reservation.setStartTime(dto.getStartTime());
        else throw new IllegalArgumentException("Start time must be before endTime");
        reservation.setEndTime(dto.getEndTime());
        reservation.setReservationText(dto.getReservationText());
        reservation.setAmountOfPeople(dto.getAmountOfPeople());
        List<Section> registerSections = new ArrayList<>();
        for (POSTSectionDTO section : dto.getSections()) {
            Optional<Section> optionalSection = sectionRepository
                    .findSectionBySectionNameAndRoomCode(section.getSectionName(), section.getRoomCode());
            if(!optionalSection.isPresent()) throw new NotFoundException("No section found with name: " + section.getSectionName());
            if (checkIfSectionIsBooked(section, dto, -1)) {
                throw new SectionAlreadyBookedException("Section is already booked/reserved for the given time period");
            }
            registerSections.add(optionalSection.get());
        }
        reservation.setSections(registerSections);
        reservation.setUser(user);
        reservationRepository.save(reservation);
        return dto;
    }

    /**
     * Changes a user reservation if it exists
     * @param reservationId
     * @param dto
     * @return edited reservation
     * @throws NotFoundException
     * @throws SectionAlreadyBookedException
     */
    public GETReservationDTO editUserReservation(long userId, long reservationId, POSTReservationDTO dto) throws NotFoundException, SectionAlreadyBookedException {
        LOGGER.info("editUserReservation(long reservationId) called with reservationId: {}", reservationId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(getUserReservation(userId, reservationId).getReservationId());
        if(!optionalReservation.isPresent()){
            throw new NotFoundException("No reservation found with id: " + reservationId);
        }
        Reservation reservation = optionalReservation.get();
        reservation.setAmountOfPeople(dto.getAmountOfPeople());
        if(dto.getReservationText() != null) reservation.setReservationText(dto.getReservationText());
        if(dto.getSections() != null && !dto.getSections().isEmpty()) {
            reservation.setSections(new ArrayList<>());
            for(POSTSectionDTO sectionDTO : dto.getSections()){
                Optional<Section> optionalSection = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
                if(optionalSection.isPresent()){
                    if(checkIfSectionIsBooked(sectionDTO, dto, reservationId)) throw new SectionAlreadyBookedException("Section is already booked/reserved for the given time period");
                    reservation.getSections().add(optionalSection.get());
                }
                else {
                    throw new NotFoundException("No section found with sectionName: " 
                        + sectionDTO.getSectionName() + ", and roomCode: " 
                        + sectionDTO.getRoomCode());
                }
            }
        }
        if(dto.getStartTime() != null && dto.getStartTime().isBefore(dto.getEndTime())) reservation.setStartTime(dto.getStartTime());
        else throw new IllegalArgumentException("Start time must be before endTime");
        if(dto.getEndTime() != null) reservation.setEndTime(dto.getEndTime());
        return new GETReservationDTO(reservationRepository.save(reservation));
    }

    /**
     * Removes an existing reservation from the user and the database.
     * Reservation specified bu reservationId.
     * @param userId
     * @param reservationId
     * @return POSTReservationDTO of the reservation that was added
     * @throws NotFoundException if user not found
     */
    public boolean deleteUserReservation(long userId, long reservationId) throws NotFoundException {
        LOGGER.info("deleteUserReservation(long userId, long reservationId) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(getUserReservation(userId, reservationId).getReservationId());
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        if(!optionalReservation.isPresent()) throw new NotFoundException("No reservation found with id: " + reservationId);
        Reservation reservation = optionalReservation.get();
        reservation.getSections().clear();
        reservation = reservationRepository.save(reservation);
        reservationRepository.delete(reservation);

        try{
            if(emailComponent != null) emailComponent.sendReservationDeleted(reservation, reservation.getUser().getEmail());
        }catch (Exception ex){
            LOGGER.info("deleteReservation(long reservationId) called with bad email: {}",reservation.getUser().getEmail());
        }

        return !reservationRepository.existsById(reservationId);
    }

    /**
     * Deletes the user from the database and all their reservations
     * @param userId
     * @return true if deleted or throws exception otherwise
     * @throws NotFoundException
     */
    public boolean deleteUser(long userId) throws NotFoundException{
        LOGGER.info("deleteUser(long userId) was called with userId: {}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        user.getReservations().stream().forEach(reservation -> reservation.getSections().clear());
        reservationRepository.saveAll(user.getReservations());
        reservationRepository.deleteAll(user.getReservations());
        user.getReservations().clear();

        if(user.getMessages() != null){
            for(Message message: user.getMessages()) message.setUser(null);
            messageRepository.saveAll(user.getMessages());
        }

        user = userRepository.save(user);
        userRepository.delete(user);
        return true;
    }

    /**
     * Gets user from Database with email and compares input password 
     * with hashed password
     * @param email username/email for the user trying to log in
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        LOGGER.info("loadUserByEmail(String email) was called with email: {}", email);

        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if(!optionalUser.isPresent()) {
            LOGGER.warn("Could not find user with email: {}. Throwing exception", email);
        }
        User user = optionalUser.get();
        String role = (user.isAdmin()) ? "ROLE_ADMIN" : "ROLE_USER";
        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(role));
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails();
        userSecurityDetails.setEmail(user.getEmail());
        userSecurityDetails.setPassword(user.getHash());
        userSecurityDetails.setGrantedAuthorities(grantedAuthorities);
        userSecurityDetails.setUserId(user.getUserId());
        if(user.getExpirationDate() != null){
            boolean expired = user.getExpirationDate().isBefore(LocalDate.now());
            userSecurityDetails.setAccountNonExpired(!expired);
        }
        return userSecurityDetails;
    }

    /**
     * Finds top 5 most users with most reservations.
     * @return List of users, empty list if no users were found
     */
    public List<GETUserDTO> getTopUsers() {
        LOGGER.info("getTopUsers() was called");
        List<User> users = userRepository.getTopUsers();
        List<GETUserDTO> userDTOs= new ArrayList<>();
        for(User user : users) {
            userDTOs.add(new GETUserDTO(user));
        }
        return userDTOs;
    }

    /**
     * Get statistics about a user, given by userId.
     * Statistics such as total hours user has booked/reserved,
     * total reservations done, favourite room and favourite section.
     * @param userId
     * @return UserStatisticsDTO
     * @throws NotFoundException
     */
    public UserStatisticsDTO getStatistics(long userId) throws NotFoundException {
        LOGGER.info("getStatistics(long userId) called with userId: {}", userId);
        if(!userRepository.existsById(userId)) {
            LOGGER.warn("Could not find user with id: {}. Throwing exception", userId);
            throw new NotFoundException("No user found with id: " + userId);
        }

        UserStatisticsDTO stats = new UserStatisticsDTO();

        Optional<Long> totalHoursOfReservationsOptional = userRepository.getSumTimeInHoursOfAllUserReservations(userId);
        Long totalHoursOfReservations = 0L;
        if(totalHoursOfReservationsOptional.isPresent()) {
            totalHoursOfReservations = totalHoursOfReservationsOptional.get();
        }
        stats.setTotalHoursOfReservations(totalHoursOfReservations);

        Optional<Integer> totalReservationsOptional = userRepository.getResevationCountOfUser(userId);
        Integer totalReservations = 0;
        if(totalReservationsOptional.isPresent()) {
            totalReservations = totalReservationsOptional.get();
        }
        stats.setTotalReservations(totalReservations);

        Optional<Long> favouriteSection = userRepository.getFavouriteSectionOfUser(userId);
        Optional<Section> sectionOptional = Optional.empty();
        if(favouriteSection.isPresent()) {
            sectionOptional = sectionRepository.findById(favouriteSection.get());
        }
        stats.setFavouriteSection((sectionOptional.isPresent())? new GETSectionDTO(sectionOptional.get()) : null);

        Optional<String> favouriteRoom = userRepository.getFavouriteRoomOfUser(userId);
        Optional<Room> roomOptional = Optional.empty();
        if(favouriteRoom.isPresent()) {
            roomOptional = roomRepository.findById(favouriteRoom.get());
        }
        stats.setFavouriteRoom((roomOptional.isPresent())? new GETRoomDTO(roomOptional.get()) : null);
        
        return stats;
    }

    /**
     * Creates a random password
     * @return randomly created password
     */
    private String createRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 30; i++) {
            int randNum = (int)Math.floor(Math.random()*3 + 1);
            switch (randNum) {
                case 1:
                    sb.append(Character.toString((char)Math.floor(Math.random()*(57-48+1) + 48)));
                    break;
                case 2:
                    sb.append(Character.toString((char)Math.floor(Math.random()*(90-65+1) + 65)));
                    break;
                case 3:
                    sb.append(Character.toString((char)Math.floor(Math.random()*(122-97+1) + 97)));
                    break;
                default:
                    sb.append(Character.toString((char)Math.floor(Math.random()*(122-97+1) + 97)));
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Checks if section is already reserved.
     * n?? complexity.
     * @param sectionDTO
     * @param reservationDTO
     * @return true if reservation already exists during specified times
     * @throws NotFoundException
     */
    private boolean checkIfSectionIsBooked(POSTSectionDTO sectionDTO, POSTReservationDTO reservationDTO, long reservationId) throws NotFoundException {
        Optional<Section> sectionOptional = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
        if(!sectionOptional.isPresent()) throw new NotFoundException("Section not found with name " + sectionDTO.getSectionName());
        Section section = sectionOptional.get();
        if(section.getReservations() == null) throw new NullPointerException("Section tries to access reservation list that is null");
        for (Reservation reservation : section.getReservations()) {
            if(reservation.getReservationId() == reservationId) continue;
            if((reservationDTO.getStartTime().isEqual(reservation.getStartTime()))
                || (reservationDTO.getEndTime().isEqual(reservation.getEndTime()))
                || (reservationDTO.getStartTime().isAfter(reservation.getStartTime()) && reservationDTO.getStartTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getEndTime().isAfter(reservation.getStartTime()) && reservationDTO.getEndTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getStartTime().isBefore(reservation.getStartTime()) && reservationDTO.getEndTime().isAfter(reservation.getEndTime()))) {
                return true;
            }
        }
        return false;
    }
}

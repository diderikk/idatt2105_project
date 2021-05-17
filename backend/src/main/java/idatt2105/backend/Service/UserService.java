package idatt2105.backend.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import idatt2105.backend.Exception.EmailAlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.GETUserDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.POSTUserDTO;
import idatt2105.backend.Repository.ReservationRepository;
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
    private ReservationRepository reservationRepository;

    @Autowired(required = false)
    private EmailComponent emailComponent;

    /**
     * Returns user from database based on given userId
     * @param userId
     * @return UserDTO
     * @throws NotFoundException if iser not found
     */
    public GETUserDTO getUser(long userId) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("No user found with id: " + userId);
        }
        User user = optionalUser.get();
        return new GETUserDTO(user);
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
    public GETUserDTO createUser(POSTUserDTO inputUser) throws EmailAlreadyExistsException, NullPointerException {
        if (inputUser.getEmail() == null || inputUser.getFirstName() == null || inputUser.getLastName() == null) {
            throw new NullPointerException("InputUserDTO object has some fields that are null");
        }
        // Check if email alrady exists
        if(userRepository.findUserByEmail(inputUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + inputUser.getEmail() + " already exists");
        }
        String randomPassword = createRandomPassword();
        User createdUser = new User();
        createdUser.setFirstName(inputUser.getFirstName());
        createdUser.setLastName(inputUser.getLastName());
        createdUser.setEmail(inputUser.getEmail());
        createdUser.setExpirationDate(inputUser.getExpirationDate());
        createdUser.setAdmin(inputUser.isAdmin());
        createdUser.setHash(passwordEncoder.encode(randomPassword));
        createdUser = userRepository.save(createdUser);

        if(emailComponent != null) emailComponent.sendPassword(createdUser.getEmail(), randomPassword);

        return new GETUserDTO(createdUser);
    }

    /**
     * Edits a given user if fields are not null.
     * Checks if email already exists before allowing the edit user;
     * @param userId
     * @param inputUser
     * @return
     * @throws NotFoundException
     * @throws EmailAlreadyExistsException
     */
    public GETUserDTO editUser(long userId, POSTUserDTO inputUser) throws NotFoundException, EmailAlreadyExistsException{
        Optional<User> optionalUser = userRepository.findById(userId);
        if(userRepository.findUserByEmail(inputUser.getEmail()).isPresent()) 
            throw new EmailAlreadyExistsException("Email " + inputUser.getEmail() + " already exists");
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(inputUser.getEmail() != null) user.setEmail(inputUser.getEmail());
            if(inputUser.getFirstName() != null) user.setFirstName(inputUser.getFirstName());
            if(inputUser.getLastName() != null) user.setLastName(inputUser.getLastName());
            if(inputUser.getExpirationDate() != null) user.setExpirationDate(inputUser.getExpirationDate());
            if(inputUser.getPhoneNumber() != null) user.setPhoneNumber(inputUser.getPhoneNumber());
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
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        return user.getReservations().stream().map(reservation -> new GETReservationDTO(reservation))
                .collect(Collectors.toList());
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
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        Reservation reservation = new Reservation();
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setReservationText(dto.getReservationText());
        reservation.setAmountOfPeople(dto.getAmountOfPeople());
        List<Section> registerSections = new ArrayList<>();
        for (POSTSectionDTO section : dto.getSections()) {
            Optional<Section> optionalSection = sectionRepository
                    .findSectionBySectionNameAndRoomCode(section.getSectionName(), section.getRoomCode());
            if(!optionalSection.isPresent()) throw new NotFoundException("No section found with name: " + section.getSectionName());
            if (checkIfSectionIsBooked(section, dto)) {
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
     * Removes an existing reservation from the user and the database.
     * Reservation specified bu reservationId.
     * @param userId
     * @param reservationId
     * @return POSTReservationDTO of the reservation that was added
     * @throws NotFoundException if user not found
     */
    public boolean removeUserReservation(long userId, long reservationId) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        if(!optionalReservation.isPresent()) throw new NotFoundException("No reservation found with id: " + reservationId);
        Reservation reservation = optionalReservation.get();
        reservation.getSections().clear();
        reservation = reservationRepository.save(reservation);
        reservationRepository.delete(reservation);
        return !reservationRepository.existsById(reservationId);
    }

    /**
     * Deletes the user from the database and all their reservations
     * @param userId
     * @return true if deleted or throws exception otherwise
     * @throws NotFoundException
     */
    public boolean deleteUser(long userId) throws NotFoundException{
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        user.getReservations().stream().forEach(reservation -> reservation.getSections().clear());
        reservationRepository.saveAll(user.getReservations());
        reservationRepository.deleteAll(user.getReservations());
        user.getReservations().clear();
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
            boolean expired = user.getExpirationDate().isAfter(LocalDate.now());
            userSecurityDetails.setAccountNonExpired(!expired);
        }
        return userSecurityDetails;
    }

    /**
     * Gets sum of all reservations user has done in the past.
     * @param userId
     * @return
     * @throws NotFoundException
     */
    public Float getSumTimeInMinutesOfAllUserReservations(long userId) throws NotFoundException {
        if(!userRepository.findById(userId).isPresent()) {
            LOGGER.warn("Could not find user with id: {}. Throwing exception", userId);
            throw new NotFoundException("No user found with id: " + userId);
        }
        Float sum = userRepository.getSumTimeInMinutesOfAllUserReservations(userId);
        if(sum != null) return sum;
        return 0f;
    }

    /**
     * Creates a random password
     * @return randomly created password
     */
    private String createRandomPassword() {
        byte[] bytes = new byte[30];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return String.valueOf(bytes);
    }

    /**
     * Checks if section is already reserved.
     * n² complexity.
     * @param sectionDTO
     * @param reservationDTO
     * @return true if reservation already exists during specified times
     * @throws NotFoundException
     */
    private boolean checkIfSectionIsBooked(POSTSectionDTO sectionDTO, POSTReservationDTO reservationDTO) throws NotFoundException {
        Optional<Section> sectionOptional = sectionRepository.findSectionBySectionNameAndRoomCode(sectionDTO.getSectionName(), sectionDTO.getRoomCode());
        if(!sectionOptional.isPresent()) throw new NotFoundException("Section not found with name " + sectionDTO.getSectionName());
        Section section = sectionOptional.get();
        if(section.getReservations() == null) throw new NullPointerException("Section tries to access reservation list that is null");
        for (Reservation reservation : section.getReservations()) {
            if((reservationDTO.getStartTime().isAfter(reservation.getStartTime()) && reservationDTO.getStartTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getEndTime().isAfter(reservation.getStartTime()) && reservationDTO.getEndTime().isBefore(reservation.getEndTime()))
                || (reservationDTO.getStartTime().isBefore(reservation.getStartTime()) && reservationDTO.getEndTime().isAfter(reservation.getEndTime()))) {
                return true;
            }
        }
        return false;
    }
}

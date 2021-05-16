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

import idatt2105.backend.Exception.EmailAlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.UserDTO;
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

    public UserDTO getUser(long userId) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("No user found with id: " + userId);
        }
        User user = optionalUser.get();
        return new UserDTO(userId, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                user.getExpirationDate(), user.isAdmin());
    }

    public UserDTO createUser(UserDTO inputUser) throws EmailAlreadyExistsException {
        if (inputUser.getEmail() == null || inputUser.getFirstName() == null || inputUser.getLastName() == null) {
            throw new NullPointerException("InputUserDTO object has some fields that are null");
        }
        // Check if email alrady exists
        if(userRepository.findUserByEmail(inputUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + inputUser.getEmail() + " already exists");
        }
        User createdUser = new User();
        createdUser.setFirstName(inputUser.getFirstName());
        createdUser.setLastName(inputUser.getLastName());
        createdUser.setEmail(inputUser.getEmail());
        createdUser.setExpirationDate(inputUser.getExpirationDate());
        createdUser.setAdmin(inputUser.isAdmin());
        createdUser.setHash(passwordEncoder.encode(createRandomPassword()));
        createdUser = userRepository.save(createdUser);

        inputUser.setUserId(createdUser.getUserId());
        return inputUser;
    }

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

    public List<GETReservationDTO> getUserReservations(long userId) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new NotFoundException("No user found with id: " + userId);
        User user = optionalUser.get();
        return user.getReservations().stream().map(reservation -> new GETReservationDTO(reservation))
                .collect(Collectors.toList());
    }

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
     * Gets user from Database with email and compares input password with hased
     * password
     * 
     * @param email username/email for the user trying to log in
     * @throws NotFoundException
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
     * Creates a random password
     * 
     * @return randomly created password
     */
    private String createRandomPassword() {
        byte[] bytes = new byte[30];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return Arrays.toString(bytes);
    }

    /**
     * Checks if section is already reserved
     * n² complexity
     * @param sectionDTO
     * @param reservationDTO
     * @return
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

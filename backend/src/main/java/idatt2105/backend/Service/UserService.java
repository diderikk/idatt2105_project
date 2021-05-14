package idatt2105.backend.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.Reservation;
import idatt2105.backend.Model.Section;
import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.GETSectionDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.UserDTO;
import idatt2105.backend.Repository.ReservationRepository;
import idatt2105.backend.Repository.SectionRepository;
import idatt2105.backend.Repository.UserRepository;

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

    public UserDTO getUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new UserDTO(userId, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                    user.getExpirationDate(), user.isAdmin());
        }
        return null;
    }

    public UserDTO createUser(UserDTO inputUser) {
        User createdUser = new User();
        if (inputUser.getEmail() == null || inputUser.getFirstName() == null || inputUser.getLastName() == null)
            return null;
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

    public boolean changePassword(ChangePasswordDTO dto) {
        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (optionalUser.isPresent() && passwordEncoder.matches(dto.getOldPassword(), optionalUser.get().getHash())) {
            User user = optionalUser.get();
            user.setHash(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public List<GETReservationDTO> getUserReservations(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getReservations().stream().map(reservation -> new GETReservationDTO(
                    reservation.getReservationId(), reservation.getReservationStartTime(),
                    reservation.getReservationEndTime(), reservation.getReservationText(),
                    reservation.getAmountOfPeople(), userId,
                    reservation.getSections().stream()
                            .map(section -> new GETSectionDTO(section.getSectionId(), section.getRoom().getRoomCode()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public POSTReservationDTO addUserReservation(long userId, POSTReservationDTO dto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Reservation reservation = new Reservation();
            reservation.setReservationStartTime(dto.getReservationStartTime());
            reservation.setReservationEndTime(dto.getReservationEndTime());
            reservation.setReservationText(dto.getReservationText());
            reservation.setAmountOfPeople(dto.getAmountOfPeople());
            List<Section> registerSections = new ArrayList<>();
            for (POSTSectionDTO section : dto.getSections()) {
                Optional<Section> optionalSection = sectionRepository.findById(section.getSectionId());
                if (optionalSection.isPresent() && checkIfNotAlreadyBooked(section, dto)) {
                    registerSections.add(optionalSection.get());
                } else
                    return null;
            }
            reservation.setSections(registerSections);
            reservation.setUser(user);
            reservationRepository.save(reservation);
            return dto;
        }
        return null;
    }

    public boolean removeUserReservation(long userId, long reservationId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent() && optionalUser.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.getSections().clear();
            reservation = reservationRepository.save(reservation);
            reservationRepository.delete(reservation);
            return true;
        }
        return false;
    }

    /**
     * Gets user from Database with email and compares input password with hased
     * password
     * 
     * @param email username/email for the user trying to log in
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("loadUserByEmail(String email) was called with email: {}", email);

        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String role = (user.isAdmin()) ? "ROLE_ADMIN" : "ROLE_USER";
            List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(role));
            UserSecurityDetails userSecurityDetails = new UserSecurityDetails();
            userSecurityDetails.setEmail(user.getEmail());
            userSecurityDetails.setPassword(user.getHash());
            userSecurityDetails.setGrantedAuthorities(grantedAuthorities);
            userSecurityDetails.setUserId(user.getUserId());
            return userSecurityDetails;
        } else {
            LOGGER.warn("Could not find user with email: {}. Throwing exception", email);
            throw new UsernameNotFoundException("Email: " + email + "was not found");
        }

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
     * n² complexity
     * 
     * @param sectionDTO
     * @param reservationDTO
     * @return
     */
    private boolean checkIfNotAlreadyBooked(POSTSectionDTO sectionDTO, POSTReservationDTO reservationDTO) {
        for (Reservation reservation : sectionRepository.findById(sectionDTO.getSectionId()).get().getReservations()) {
            if ((reservationDTO.getReservationStartTime().isAfter(reservation.getReservationStartTime())
                    || reservationDTO.getReservationStartTime().isEqual(reservation.getReservationStartTime()))
                    && (reservationDTO.getReservationEndTime().isBefore(reservation.getReservationEndTime())
                            || reservationDTO.getReservationEndTime().isEqual(reservation.getReservationEndTime())))
                return false;
        }
        return true;
    }
}
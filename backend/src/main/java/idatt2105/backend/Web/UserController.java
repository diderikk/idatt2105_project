package idatt2105.backend.Web;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import idatt2105.backend.Exception.AlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Reservation.POSTReservationDTO;
import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import idatt2105.backend.Model.DTO.Section.GETSectionDTO;
import idatt2105.backend.Model.DTO.User.GETUserDTO;
import idatt2105.backend.Model.DTO.User.POSTUserDTO;
import idatt2105.backend.Model.DTO.User.UserStatisticsDTO;
import idatt2105.backend.Service.UserService;
import javassist.NotFoundException;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{user_id}")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETUserDTO> getUser(@PathVariable("user_id") long userId){
        try {
            GETUserDTO user = userService.getUser(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETUserDTO>> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETUserDTO> createUser(@RequestBody POSTUserDTO inputUser){
        try {
            GETUserDTO createdUser = userService.createUser(inputUser);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (AlreadyExistsException | NullPointerException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETUserDTO> editUser(@PathVariable("user_id") long userId, @RequestBody POSTUserDTO inputUser){
        try {
            GETUserDTO createdUser = userService.editUser(userId, inputUser);
            return new ResponseEntity<>(createdUser, HttpStatus.OK);
        } catch (AlreadyExistsException | NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{user_id}/password")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> changePassword(@PathVariable("user_id") long userId, @RequestBody ChangePasswordDTO dto) {
        try {
            boolean successful = userService.changePassword(dto);
            if(successful) return new ResponseEntity<>(HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{user_id}/reservations")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETReservationDTO>> getUserReservations(@PathVariable("user_id") long userId){
        try {
            return new ResponseEntity<>(userService.getUserReservations(userId), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{user_id}/reservations/{reservation_id}")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETReservationDTO> getUserReservation(@PathVariable("user_id") long userId, @PathVariable("reservation_id") long reservationId){
        try{
            return new ResponseEntity<>(userService.getUserReservation(userId, reservationId), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{user_id}/reservations/sort")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETReservationDTO>> getSortedAndFilteredUserReservations(@PathVariable("user_id") long userId, @RequestBody SortingDTO dto){
        return new ResponseEntity<>(userService.getSortedAndFilteredReservations(userId, dto), HttpStatus.OK);
    }

    @PostMapping("/{user_id}/reservations")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<POSTReservationDTO> addUserReservation(@PathVariable("user_id") long userId, @RequestBody POSTReservationDTO dto){
        try {
            POSTReservationDTO newDto = userService.addUserReservation(userId, dto);
            return new ResponseEntity<>(newDto, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SectionAlreadyBookedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{user_id}/reservations/{reservation_id}")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETReservationDTO> editUserReservation(@PathVariable("user_id") long userId, @PathVariable("reservation_id") long reservationId, @RequestBody POSTReservationDTO dto){
        try{
            GETReservationDTO newDto = userService.editUserReservation(userId, reservationId, dto);
            return new ResponseEntity<>(newDto, HttpStatus.OK);
        }catch(NotFoundException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(SectionAlreadyBookedException ex ){
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{user_id}/reservations/{reservation_id}")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUserReservation(@PathVariable("user_id") long userId, @PathVariable("reservation_id") long reservationId){
        try {
            boolean successful = userService.deleteUserReservation(userId, reservationId);
            if(successful) return new ResponseEntity<>(HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") long userId){
        try{
            boolean successful = userService.deleteUser(userId);
            if(successful) return new ResponseEntity<>(HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
    }

    @GetMapping("/statistics/top-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETUserDTO>> getTopUsers(){
        return new ResponseEntity<>(userService.getTopUsers(), HttpStatus.OK);
    }

    @GetMapping("/{user_id}/statistics")
    @PreAuthorize("#userId == principal.userId or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserStatisticsDTO> getStatistics(@PathVariable("user_id") long userId){
        try{
            UserStatisticsDTO userStatistics = userService.getStatistics(userId);
            return new ResponseEntity<>(userStatistics, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
    }
}

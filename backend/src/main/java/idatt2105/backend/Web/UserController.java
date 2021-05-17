package idatt2105.backend.Web;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import idatt2105.backend.Exception.EmailAlreadyExistsException;
import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.UserDTO;
import idatt2105.backend.Service.UserService;
import javassist.NotFoundException;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{user_id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("user_id") long userId){
        try {
            UserDTO user = userService.getUser(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO inputUser){
        try {
            UserDTO createdUser = userService.createUser(inputUser);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException | NullPointerException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{user_id}/password")
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
    public ResponseEntity<List<GETReservationDTO>> getUserReservations(@PathVariable("user_id") long userId){
        try {
            return new ResponseEntity<>(userService.getUserReservations(userId), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{user_id}/reservations")
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

    @DeleteMapping("/{user_id}/reservations/{reservation_id}")
    public ResponseEntity<String> removeUserReservation(@PathVariable("user_id") long userId, @PathVariable("reservation_id") long reservationId){
        try {
            boolean successful = userService.removeUserReservation(userId, reservationId);
            if(successful) return new ResponseEntity<>(HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

package idatt2105.backend.Web;

import java.util.List;

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

import idatt2105.backend.Model.DTO.ChangePasswordDTO;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.UserDTO;
import idatt2105.backend.Service.UserService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{user_id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("user_id") long userId){
        UserDTO user = userService.getUser(userId);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO inputUser){
        UserDTO createdUser = userService.createUser(inputUser);
        if(createdUser == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{user_id}/password")
    public ResponseEntity<String> changePassword(@PathVariable("user_id") long userId, @RequestBody ChangePasswordDTO dto) {
        boolean successful = userService.changePassword(dto);
        if(!successful){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{user_id}/reservations")
    public ResponseEntity<List<GETReservationDTO>> getAllUserReservations(@PathVariable("user_id") long userId){
        if(userService.getUser(userId) == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userService.getUserReservations(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{user_id}/reservations/{reservation_id}")
    public ResponseEntity<String> removeUserReservation(@PathVariable("user_id") long userId, @PathVariable("reservation_id") long reservationId){
        boolean successful = userService.removeUserReservation(userId, reservationId);
        if(!successful){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

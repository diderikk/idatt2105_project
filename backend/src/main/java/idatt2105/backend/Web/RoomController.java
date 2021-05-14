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

import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Model.DTO.RoomDTO;
import idatt2105.backend.Service.RoomService;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRooms() {
        List<RoomDTO> rooms = roomService.getRooms();
        if (rooms == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{room_code}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable("room_code") String roomCode) {
        RoomDTO room = roomService.getRoom(roomCode);
        if (room == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO room = roomService.createRoom(roomDTO);
        if (room == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping("/{room_code}/reservations")
    public ResponseEntity<List<GETReservationDTO>> getReservationsOfRoom(@PathVariable("room_code") String roomCode) {
        List<GETReservationDTO> reservations = roomService.getReservationsOfRoom(roomCode);
        if (reservations == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{room_id}/sections/{section_id}/reservations")
    public ResponseEntity<List<GETReservationDTO>> getReservationsOfSection(@PathVariable("room_code") String roomCode, @PathVariable("section_id") long sectionId) {
        List<GETReservationDTO> reservations = roomService.getReservationsOfSection(roomCode, sectionId);
        if (reservations == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping("/{room_code}/sections")
    public ResponseEntity<RoomDTO> addSectionToRoom(@PathVariable("room_code") String roomCode, @RequestBody POSTSectionDTO sectionDTO) {
        RoomDTO roomDTO = roomService.addSectionToRoom(roomCode, sectionDTO);
        if (roomDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{room_code}")
    public ResponseEntity<RoomDTO> deleteRoom(@PathVariable("room_code") String roomCode) {
        if (roomService.deleteRoom(roomCode)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{room_code}/sections/{section_id}")
    public ResponseEntity<RoomDTO> deleteRoom(@PathVariable("room_code") String roomCode, @PathVariable("section_id") long sectionId) {
        if (roomService.deleteSectionOfRoom(roomCode, sectionId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

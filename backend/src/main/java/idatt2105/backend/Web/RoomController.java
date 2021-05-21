package idatt2105.backend.Web;

import java.util.List;

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
import idatt2105.backend.Exception.SectionNameInRoomAlreadyExistsException;
import idatt2105.backend.Exception.SectionNotOfThisRoomException;
import idatt2105.backend.Model.DTO.TimeIntervalDTO;
import idatt2105.backend.Model.DTO.MessageDTO;
import idatt2105.backend.Model.DTO.Reservation.GETReservationDTO;
import idatt2105.backend.Model.DTO.Room.GETRoomDTO;
import idatt2105.backend.Model.DTO.Room.POSTRoomDTO;
import idatt2105.backend.Model.DTO.Room.RoomStatisticsDTO;
import idatt2105.backend.Model.DTO.Section.AvailableSectionsDTO;
import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import idatt2105.backend.Service.RoomService;
import javassist.NotFoundException;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<List<GETRoomDTO>> getRooms() {
        List<GETRoomDTO> rooms = roomService.getRooms();
        if (rooms == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{room_code}")
    public ResponseEntity<GETRoomDTO> getRoom(@PathVariable("room_code") String roomCode) {
        try {
            GETRoomDTO room = roomService.getRoom(roomCode);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{room_code}/messages")
    public ResponseEntity<List<MessageDTO>> getRoomMessages(@PathVariable("room_code") String roomCode){
        try{
            return new ResponseEntity<>(roomService.getRoomMessages(roomCode), HttpStatus.OK);
        } catch(NotFoundException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/available")
    public ResponseEntity<AvailableSectionsDTO> getAvailableRooms(@RequestBody TimeIntervalDTO dto) {
        return new ResponseEntity<>(roomService.getAvailableRooms(dto), HttpStatus.OK);
    }

    @PostMapping("/available/{reservation_id}")
    public ResponseEntity<AvailableSectionsDTO> getAvailableRooms(@PathVariable("reservation_id") long reservationId,@RequestBody TimeIntervalDTO dto) {
        return new ResponseEntity<>(roomService.getAvailableRooms(dto, reservationId), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETRoomDTO> createRoom(@RequestBody POSTRoomDTO roomDTO) {
        try{
            GETRoomDTO room = roomService.createRoom(roomDTO);
            return new ResponseEntity<>(room, HttpStatus.CREATED);
        } catch(AlreadyExistsException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    @PostMapping("/{room_code}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETRoomDTO> editRoom(@PathVariable("room_code") String roomCode, @RequestBody POSTRoomDTO roomDTO){
        try{
            GETRoomDTO room = roomService.editRoom(roomCode, roomDTO);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch(IllegalArgumentException | AlreadyExistsException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
    }

    @GetMapping("/{room_code}/reservations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETReservationDTO>> getReservationsOfRoom(@PathVariable("room_code") String roomCode) {
        try {
            List<GETReservationDTO> reservations = roomService.getReservationsOfRoom(roomCode);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{room_code}/sections/{section_id}/reservations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETReservationDTO>> getReservationsOfSection(@PathVariable("room_code") String roomCode, @PathVariable("section_id") long sectionId) {
        try {
            List<GETReservationDTO> reservations = roomService.getReservationsOfSection(roomCode, sectionId);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SectionNotOfThisRoomException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{room_code}/sections")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETRoomDTO> addSectionToRoom(@PathVariable("room_code") String roomCode, @RequestBody POSTSectionDTO sectionDTO) {
        //Return HTTP 400 if roomCode does not match roomCode in sectionDTO
        if(!roomCode.equals(sectionDTO.getRoomCode())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            GETRoomDTO roomDTO = roomService.addSectionToRoom(sectionDTO);
            return new ResponseEntity<>(roomDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SectionNameInRoomAlreadyExistsException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{room_code}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETRoomDTO> deleteRoom(@PathVariable("room_code") String roomCode) {
        try {
            if (roomService.deleteRoom(roomCode)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{room_code}/sections/{section_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GETRoomDTO> deleteSectionOfRoom(@PathVariable("room_code") String roomCode, @PathVariable("section_id") long sectionId) {
        try {
            if (roomService.deleteSectionOfRoom(roomCode, sectionId)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SectionNotOfThisRoomException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statistics/top-rooms")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GETRoomDTO>> getTopRooms(){
        return new ResponseEntity<>(roomService.getTopRooms(), HttpStatus.OK);
    }

    @GetMapping("/{room_code}/statistics")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomStatisticsDTO> getStatistics(@PathVariable("room_code") String roomCode){
        try {
            return new ResponseEntity<>(roomService.getStatistics(roomCode), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

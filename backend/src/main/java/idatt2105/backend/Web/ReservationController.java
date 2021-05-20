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

import idatt2105.backend.Exception.SectionAlreadyBookedException;
import idatt2105.backend.Model.DTO.GETReservationDTO;
import idatt2105.backend.Model.DTO.POSTReservationDTO;
import idatt2105.backend.Model.DTO.SortingDTO;
import idatt2105.backend.Service.ReservationService;
import javassist.NotFoundException;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<GETReservationDTO>> getReservations(){
        return new ResponseEntity<>(reservationService.getReservations(), HttpStatus.OK);
    }

    @GetMapping("/{reservation_id}")
    public ResponseEntity<GETReservationDTO> getReservation(@PathVariable("reservation_id") long reservationId){
        try {
            GETReservationDTO reservation = reservationService.getReservation(reservationId);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sort")
    public ResponseEntity<List<GETReservationDTO>> getSortedAndFilteredReservations(@RequestBody SortingDTO dto){
        return new ResponseEntity<>(reservationService.getSortedAndFilteredReservations(dto), HttpStatus.OK);
    }

    @PostMapping("/{reservation_id}")
    public ResponseEntity<GETReservationDTO> editReservation(@PathVariable("reservation_id") long reservationId, @RequestBody POSTReservationDTO dto){
        try {
            GETReservationDTO reservation = reservationService.editReservation(reservationId, dto);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (NotFoundException | SectionAlreadyBookedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{reservation_id}")
    public ResponseEntity<Boolean> deleteReservation(@PathVariable("reservation_id") long reservationId){
        try {
            boolean successful = reservationService.deleteReservation(reservationId);
            if(successful) return new ResponseEntity<>(HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

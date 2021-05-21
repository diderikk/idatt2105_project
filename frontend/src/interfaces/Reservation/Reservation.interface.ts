import POSTReservation from "./POSTReservation.interface";

export default interface Reservation extends POSTReservation {
  reservationId: number;
}

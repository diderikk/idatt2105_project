import CreateReservation from "./POSTReservation.interface";

export default interface Reservation extends CreateReservation {
  id: number;
}

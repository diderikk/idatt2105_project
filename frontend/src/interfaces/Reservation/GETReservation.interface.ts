import BaseUser from "../User/BaseUser.interface";
import POSTReservation from "./POSTReservation.interface";
import Reservation from "./Reservation.interface";

export default interface GETReservation extends Reservation {
  user: BaseUser;
}

import BaseUser from "../User/BaseUser.interface";
import POSTReservation from "./POSTReservation.interface";

export default interface GETReservation extends POSTReservation {
  reservationId: number;
  user: BaseUser;
}

import BaseUser from "../User/BaseUser.interface";
import ReservationForm from "./ReservationForm.interface";

export default interface ReservationCard extends ReservationForm {
  reservationId: number;
  user: BaseUser;
}

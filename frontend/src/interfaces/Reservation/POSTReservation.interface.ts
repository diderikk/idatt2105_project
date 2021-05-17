import POSTSection from "../Section/POSTSection.interface";

export default interface CreateReservation {
  sections: POSTSection[];
  reservationText: string;
  //Of format "yyyy-MM-dd HH:mm"
  startTime: string;
  //Of format "yyyy-MM-dd HH:mm"
  endTime: string;
  amountOfPeople: number;
}

export default interface ReservationForm {
  roomCode: string;
  sections: string[];
  reservationText: string;
  //Of format "HH:mm"
  startTime: string;
  //Of format "HH:mm"
  endTime: string;
  amountOfPeople: string;
  //Of format "yyyy-MM-dd
  startDate: string;
  //Of format "yyyy-MM-dd
  endDate: string;
}

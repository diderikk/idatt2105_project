import ReservationForm from "@/interfaces/Reservation/ReservationForm.interface";
import POSTReservation from "@/interfaces/Reservation/POSTReservation.interface";

export const reservationFormToPOSTReservtion = (
  reservation: ReservationForm
): POSTReservation => {
  const sections = reservation.sections.map((s: string) => {
    return { sectionName: s, roomCode: reservation.roomCode };
  });
  return {
    amountOfPeople: +reservation.amountOfPeople,
    startTime: reservation.startDate + " " + reservation.startTime,
    endTime: reservation.endDate + " " + reservation.endTime,
    reservationText: reservation.reservationText,
    sections: sections,
  };
};

export const POSTReservationToResrevationForm = (
  reservation: POSTReservation
): ReservationForm => {
  const sections = reservation.sections.map((s) => {
    return s.sectionName;
  });
  const splitStartTime = reservation.startTime.split("T");
  const splitEndTime = reservation.endTime.split("T");
  return {
    amountOfPeople: "" + reservation.amountOfPeople,
    roomCode: reservation.sections[0].roomCode,
    reservationText: reservation.reservationText,
    startDate: splitStartTime[0],
    startTime: splitStartTime[1].substring(0, 5),
    endDate: splitEndTime[0],
    endTime: splitEndTime[1].substring(0, 5),
    sections: sections,
  };
};

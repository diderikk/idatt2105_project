import ReservationForm from "@/interfaces/Reservation/ReservationForm.interface";
import POSTReservation from "@/interfaces/Reservation/POSTReservation.interface";
import AvailableRoom from "@/interfaces/Room/AvailableRoom.interface";
import GETRoom from "@/interfaces/Room/GETRoom.interface";
import GETSection from "@/interfaces/Section/GETSection.interface";
import SectionWithDisable from "@/interfaces/Section/SectionWithDisable.interface";
import GETReservation from "@/interfaces/Reservation/GETReservation.interface";

export const reservationFormToPOSTReservtion = (
  reservation: ReservationForm
): POSTReservation => {
  const sections = reservation.sections.map((s: SectionWithDisable) => {
    return { sectionName: s.sectionName, roomCode: reservation.roomCode };
  });
  return {
    amountOfPeople: +reservation.amountOfPeople,
    startTime: reservation.startDate + " " + reservation.startTime,
    endTime: reservation.endDate + " " + reservation.endTime,
    reservationText: reservation.reservationText,
    sections: sections,
  };
};

export const GETReservationToReservationForm = (
  reservation: GETReservation
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
    sections: sections.map((section) => {
      return {
        sectionName: section,
        isDisabled: false,
      };
    }) as SectionWithDisable[],
  };
};

export const AvailableRoomsToReservationForm = (availableRooms: {
  rooms: GETRoom[];
  availableSections: { sectionId: number }[];
}): AvailableRoom[] => {
  return availableRooms.rooms.map((room) => {
    return {
      roomCode: room.roomCode,
      sections: room.sections.map((section) => {
        return {
          sectionName: section.sectionName,
          isDisabled: !checkIfSectionAvailable(
            section,
            availableRooms.availableSections
          ),
        };
      }),
    };
  }) as AvailableRoom[];
};

const checkIfSectionAvailable = (
  section: GETSection,
  availableSections: { sectionId: number }[]
): boolean => {
  for (const availableSection of availableSections)
    if (section.sectionId === availableSection.sectionId) return true;
  return false;
};

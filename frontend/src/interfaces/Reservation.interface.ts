import Section from "./Sections/Section.interface";

export default interface Reservation {
  reservationId: number;
  reservationStartTime: string;
  reservationEndTime: string;
  reservationText: string;
  amountOfPeople: number;
  userId: number;
  sections: Section[];
}

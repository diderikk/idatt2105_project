import Room from "../Room/Room.interface";
import Section from "../Section/Section.interface";

export default interface UserStats {
    totalHoursOfReservations: number;
    totalReservations: number;
    favouriteRoom: Room;
    favouriteSection: Section;
}
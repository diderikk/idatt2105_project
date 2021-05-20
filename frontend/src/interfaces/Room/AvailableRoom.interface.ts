import SectionWithDisable from "../Section/SectionWithDisable.interface";

export default interface AvailableRooms {
    roomCode: String;
    sections: Array<SectionWithDisable>
}
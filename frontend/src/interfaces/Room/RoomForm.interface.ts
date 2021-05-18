import Section from "../Section/Section.interface";

export default interface RoomForm {
  roomCode: string;
  sections: Array<Section>;
}

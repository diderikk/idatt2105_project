import Section from "./Section/Section.interface";

export default interface Room {
  roomCode: string;
  sections: Array<Section>;
}

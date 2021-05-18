import POSTSection from "../Section/POSTSection.interface";

export default interface Room {
  roomCode: string;
  sections: Array<POSTSection>;
}
import GETSection from "../Section/GETSection.interface";

export default interface GETRoom {
    roomCode: string;
    sections: Array<GETSection>;
  }
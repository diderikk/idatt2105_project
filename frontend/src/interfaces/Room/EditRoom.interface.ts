import Room from "./Room.interface";

export default interface EditRoom extends Room  {
    originalRoomCode: string
}
import Room from "@/interfaces/Room/Room.interface";
import RoomForm from "@/interfaces/Room/RoomForm.interface";
import POSTSection from "@/interfaces/Section/POSTSection.interface";
import Section from "@/interfaces/Section/Section.interface";

export const RoomFormToRoom = (roomForm: RoomForm): Room => {
  const roomSections = roomForm.sections.map((section) => {
    return {
      roomCode: roomForm.roomCode,
      sectionName: section.sectionName,
    } as POSTSection;
  });
  return {
    roomCode: roomForm.roomCode,
    sections: roomSections,
  } as Room;
};


export const RoomToRoomForm = (room: Room): RoomForm => {
    const sectionsWithoutRoomCode = room.sections.map((section) => {
        return {
            sectionName: section.sectionName
        } as Section
    });

    return {
        roomCode: room.roomCode,
        sections: sectionsWithoutRoomCode
    } as RoomForm
}

//Have to store
export default interface CreateReservation {
  roomCode: string;
  sections: string[];
  description: string;
  startDate: string;
  startTime: string;
  endDate: string;
  endTime: string;
  limit: string;
}

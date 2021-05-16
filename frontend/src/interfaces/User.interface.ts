import CreateUser from "./CreateUser.interface";

export default interface User extends CreateUser {
  id: number;
  isAdmin: boolean;
}

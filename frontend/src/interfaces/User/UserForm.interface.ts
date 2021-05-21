import BaseUser from "./BaseUser.interface";

export default interface UserForm extends BaseUser {
  isAdmin: boolean;
  expirationDate: string;
}

import UserForm from "@/interfaces/User/UserForm.interface";
import User from "@/interfaces/User/User.interface";

export const UserToUserForm = (user: User): UserForm => {
  return {
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    phoneNumber: user.phoneNumber,
    expirationDate: user.expirationDate,
    isAdmin: user.isAdmin,
  };
};

export const UserFormToUser = (user: UserForm, userId?: number): User => {
  return {
    userId: userId ?? -1,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    phoneNumber: user.phoneNumber,
    expirationDate: user.expirationDate,
    isAdmin: user.isAdmin,
  };
};

import UserForm from "@/interfaces/User/UserForm.interface";
import User from "@/interfaces/User/User.interface";

export const UserToUserForm = (user: User): UserForm => {
  const phoneNumber = user.phoneNumber.split(" ");
  return {
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    phoneNationalCode: phoneNumber[0],
    phoneNumber: phoneNumber[1],
    expirationDate: user.expirationDate,
  };
};

export const UserFormToUser = (
  user: UserForm,
  userId?: number,
  isAdmin?: boolean
): User => {
  return {
    userId: userId ?? -1,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    phoneNumber: `+${user.phoneNationalCode} ${user.phoneNumber}`,
    expirationDate: user.expirationDate,
    isAdmin: isAdmin ?? false,
  };
};

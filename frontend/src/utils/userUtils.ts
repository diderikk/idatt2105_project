import UserForm from "@/interfaces/User/UserForm.interface";
import User from "@/interfaces/User/User.interface";

/**
 * Creates a new UserForm object from the User object
 * @param user
 * @returns UserForm
 */
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

/**
 * Creates a new User object from the UserForm object
 * @param user
 * @param userId is optional, and -1 is assigned as id if it is not given
 * @returns User
 */
export const UserFormToUser = (user: UserForm, userId?: number): User => {
  return {
    userId: userId ?? -1,
    ...user,
  };
};

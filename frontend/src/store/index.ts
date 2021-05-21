import User from "@/interfaces/User/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import SnackbarStatus from "../enum/SnackbarStatus.enum";
import backend from "../backend";
import POSTReservation from "@/interfaces/Reservation/POSTReservation.interface";
import Reservation from "@/interfaces/Reservation/Reservation.interface";
import ReservationSorting from "@/interfaces/Reservation/ReservationSorting.interface";
import Room from "@/interfaces/Room/Room.interface";
import EditRoom from "@/interfaces/Room/EditRoom.interface";
import { UserToUserForm } from "@/utils/userUtils";
import TimeInterval from "@/interfaces/TimeInterval.interface";
import RoomStats from "@/interfaces/Room/RoomStats.interface";
import State from "@/interfaces/State.interface";
import UserForm from "@/interfaces/User/UserForm.interface";
import UserStats from "@/interfaces/User/UserStats.interface";
import GETReservation from "@/interfaces/Reservation/GETReservation.interface";
import GETRoom from "@/interfaces/Room/GETRoom.interface";
import Message from "@/interfaces/Message.interface";
import Stomp from "stompjs";
import GETAvailableSections from "@/interfaces/Section/GETAvailableSections.interface";
import GETSection from "@/interfaces/Section/GETSection.interface";

export const key: InjectionKey<Store<State>> = Symbol();

export const store: Store<State> = createStore<State>({
  state: {
    user: localStorage.getItem("user") || "",
    token: localStorage.getItem("token") || "",
    snackbar: {
      content: "",
      status: SnackbarStatus.NONE,
    },
  },
  mutations: {
    setToken(state, token: string) {
      state.token = token;
      state.token === ""
        ? localStorage.removeItem("token")
        : localStorage.setItem("token", state.token);
    },
    setUser(state, user) {
      state.user = JSON.stringify(user);
      user === ""
        ? localStorage.removeItem("user")
        : localStorage.setItem("user", state.user);
    },
    setSnackbar(state, snackbar) {
      state.snackbar = snackbar;
    },
    setSnackbarStatus(state, status: SnackbarStatus) {
      state.snackbar.status = status;
    },
  },
  getters: {
    getUser: (state): User =>
      state.user === ""
        ? {
            id: 0,
            firstname: "",
            lastname: "",
            email: "",
            phoneNumber: "",
            isAdmin: false,
            expirationDate: "",
          }
        : JSON.parse(state.user),
    isUserLoggedIn: (state) => !!state.token,
    getSnackbar: (state) => state.snackbar,
  },
  actions: {
    /**
     * Sends a call to backend at POST /users
     * @param user
     * @returns Promise<boolean>
     */
    async createUser({ commit, getters }, user: UserForm): Promise<boolean> {
      //Not letting users that aren't admins create users
      if (!getters.getUser.isAdmin) {
        commit("setSnackbar", {
          content: "Only admins can create users",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post("/users", user);
        commit("setSnackbar", {
          content: `User with email: ${user.email} created`,
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          if (error.response.status === 500) {
            commit("setSnackbar", {
              content: "Email is not valid",
              status: SnackbarStatus.ERROR,
            });
          } else {
            commit("setSnackbar", {
              content: "Could not create user",
              status: SnackbarStatus.ERROR,
            });
          }
        }
        return false;
      }
    },
    /**
     * Sends to call to backend at POST /login
     * @param user an object containing email as string and password as string
     * @returns Promise<boolean>
     */
    async login({ commit }, user: { email: string; password: string }) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.post("/login", user);
        backend.defaults.headers["Authorization"] =
          "Bearer " + response.data.token;
        commit("setToken", response.data.token);
        const userResponse = await backend.get(
          `/users/${response.data.userId}`
        );
        commit("setUser", userResponse.data);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return true;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Error could not log in",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    /**
     * Sends call to backend at POST /users/${user}
     * @param user containing userId aswell as all attributes in UserForm interface
     * @returns Promise<boolean>
     */
    async editUser({ commit }, user: User) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        console.log(user);
        await backend.post(`/users/${user.userId}`, UserToUserForm(user));
        commit("setSnackbar", {
          content: `User edited`,
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not edit user",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    /**
     * Sends call to backend at DELETE /users/${userId}
     * @param userId
     * @returns Promise<boolean>
     */
    async deleteUser({ commit }, userId: number): Promise<boolean> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.delete(`/users/${userId}`);
        commit("setSnackbar", {
          content: `User deleted`,
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not delete user",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    /**
     * Removes token and user from state and localStorage, removes the Authorization header in the backend axios instance
     */
    logout({ commit }) {
      commit("setToken", "");
      commit("setUser", "");
      delete backend.defaults.headers["Authorization"];
    },
    /**
     * Sends call to backend at GET /users/${userId}
     * @param userId
     * @returns Promise<User | null> User if successful, and null if error
     */
    async getUser({ commit }, userId: number): Promise<User | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/users/${userId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not get user",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Sends call to backend at GET /users/${userId}/statistics
     * @param userId
     * @returns Promise<UserStats | null> UserStats if successful, and null if error
     */
    async getUserStatistics(
      { commit },
      userId: number
    ): Promise<UserStats | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/users/${userId}/statistics`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not get user statistics",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Calls backend at GET /users
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @returns Promise<User[] | null>, User[] if successful, or null if error
     */
    async getUsers({ commit }, editSnackbar?: boolean): Promise<User[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/users");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not get users",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Calls backend at POST /users/${getters.getUser.userId}/reservations
     * @param reservation, contains all necessary information about an reservation
     * @returns Promise<boolean>
     */
    async createReservation({ commit, getters }, reservation: POSTReservation) {
      try {
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
        await backend.post(
          `/users/${getters.getUser.userId}/reservations`,
          reservation
        );

        commit("setSnackbar", {
          content: "Reservation created",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          if (error.response.status === 400) {
            commit("setSnackbar", {
              content: "Already occupied",
              status: SnackbarStatus.ERROR,
            });
          } else {
            commit("setSnackbar", {
              content: "Could not create reservation",
              status: SnackbarStatus.ERROR,
            });
          }
        }
        return false;
      }
    },
    /**
     * Calls backend at POST /users/${getters.getUser.userId}/reservations/${reservation.reservationId}
     * @param reservation, just like POSTReservation but also includes reservationId
     * @returns Promise<boolean>
     */
    async editReservation({ commit }, reservation: Reservation) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        if (!store.getters.getUser.isAdmin)
          await backend.post(
            `/users/${store.getters.getUser.userId}/reservations/${reservation.reservationId}`,
            reservation
          );
        else
          await backend.post(
            `/reservations/${reservation.reservationId}`,
            reservation
          );
        commit("setSnackbar", {
          content: "Reservation edited",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Reservation could not be edited",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    /**
     * Calls backend at DELETE /users/${getters.getUser.userId}/reservations/${reservation.reservationId}
     * @param reservationId
     * @returns Promise<boolean>
     */
    async deleteReservation({ commit }, reservationId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        if (!store.getters.getUser.isAdmin)
          await backend.delete(
            `/users/${store.getters.getUser.userId}/reservations/${reservationId}`
          );
        else await backend.delete(`/reservations/${reservationId}`);

        commit("setSnackbar", {
          content: "Reservation deleted",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Reservation could not be deleted",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    /**
     * Calls backend GET /users/${getters.getUser.userId}/reservations/${reservation.reservationId}
     * @param reservationId
     * @returns Promise<GETReservation|null>
     */
    async getReservation(
      { commit },
      reservationId: number
    ): Promise<GETReservation | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        let response;
        if (!store.getters.getUser.isAdmin)
          response = await backend.get(
            `/users/${store.getters.getUser.userId}/reservations/${reservationId}`
          );
        else response = await backend.get(`/reservations/${reservationId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find a reservation",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * If admin and no sortingConfig is given calls backend at GET /reservation
     * If admin and sortingConfig is given calls backend at GET /reservations/sort
     * If user and no sortingConfig is given calls backend at GET /users/${currentUser.userId}/reservations
     * If user and sortingConfig is given calls backend at GET /users/${currentUser.userId}/reservations/sort
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @param sortingConfig optional, how the reservations shuld be sorted
     * @returns Promise<GETReservation[]|null>
     */
    async getReservations(
      { commit },
      editSnackbar: boolean,
      sortingConfig?: ReservationSorting
    ): Promise<GETReservation[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        let response;
        const currentUser = store.getters.getUser;
        if (sortingConfig === undefined) {
          if (currentUser.isAdmin) {
            response = await backend.get("/reservations");
          } else {
            response = await backend.get(
              `/users/${currentUser.userId}/reservations`
            );
          }
        } else {
          if (currentUser.isAdmin) {
            response = await backend.post("/reservations/sort", sortingConfig);
          } else {
            response = await backend.post(
              `/users/${currentUser.userId}/reservations/sort`,
              sortingConfig
            );
          }
        }

        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find any reservations",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Calls backend at GET /rooms
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @returns Promise<GETRoom[] | null>
     */
    async getRooms(
      { commit },
      editSnackbar?: boolean
    ): Promise<GETRoom[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/rooms");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find any rooms",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Calls backend at POST /rooms/available
     * @param timeInterval
     * @returns Promise<GETAvailableSections | null>
     */
    async getAvailableRooms(
      { commit },
      timeInterval: { times: TimeInterval; reservationId?: number }
    ): Promise<GETAvailableSections | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        let response;
        if (timeInterval.reservationId === undefined)
          response = await backend.post("/rooms/available", timeInterval.times);
        else
          response = await backend.post(
            `/rooms/available/${timeInterval.reservationId}`,
            timeInterval.times
          );
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find any rooms",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Calls backend at GET /rooms/${roomCode}
     * @param roomCode
     * @returns Promise<GETRoom|null>
     */
    async getRoom({ commit }, roomCode: string): Promise<GETRoom | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/rooms/${roomCode}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find a room",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    async getRoomStatistics({ commit }, roomCode: string): Promise<RoomStats | null> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/rooms/${roomCode}/statistics`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if(error !== null){
          commit("setSnackbar", {
            content: "Could not get room statistics",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
    /**
     * Gets messages that have been written in a room chat
     * @param roomCode
     * @returns a list of messages
     */
    async getRoomMessages({ commit }, roomCode: string): Promise<Message[]> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/rooms/${roomCode}/messages`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not find a room",
            status: SnackbarStatus.ERROR,
          });
        }
        return [];
      }
    },
    /**
     * Connects to the chat room
     * @param connectInfo roomCode and
     * @return Promise<boolean>
     */
    async connectChat(
      { commit },
      connectInfo: {
        roomCode: string;
        stompClient: Stomp.Client;
        messages: Message[];
      }
    ): Promise<boolean> {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      await connectInfo.stompClient.connect(
        {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        () => {
          connectInfo.stompClient.subscribe(
            `/api/v1/chat/${connectInfo.roomCode}/messages`,
            (message) => {
              connectInfo.messages.push(JSON.parse(message.body));
            }
          );
          commit("setSnackbarStatus", SnackbarStatus.NONE);
          return true;
        },
        (error) => {
          if (error !== null) {
            commit("setSnackbar", {
              content: "Could not connect to room chat",
              status: SnackbarStatus.ERROR,
            });
          }
          return false;
        }
      );
      return false;
    },
    /** Calls backend at POST /rooms
     * @param room
     * @returns Promise<boolean>
     */
    async createRoom({ commit }, room: Room) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post("/rooms", room);

        commit("setSnackbar", {
          content: "Room created",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          if (error.response.status === 400) {
            commit("setSnackbar", {
              content: "Room code is already occupied",
              status: SnackbarStatus.ERROR,
            });
          } else {
            commit("setSnackbar", {
              content: "Could not create room",
              status: SnackbarStatus.ERROR,
            });
          }
        }
        return false;
      }
    },
    /**
     * Calls backend at POST /rooms/${roomCode}
     * @param editRoom
     * @returns Promise<boolean>
     */
    async editRoom({ commit }, editRoom: EditRoom) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post(`/rooms/${editRoom.originalRoomCode}`, {
          roomCode: editRoom.roomCode,
          sections: editRoom.sections,
        } as Room);

        commit("setSnackbar", {
          content: "Room edited",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          if (error.response.status === 400) {
            commit("setSnackbar", {
              content: "Room code is already occupied",
              status: SnackbarStatus.ERROR,
            });
          } else if (error.response.status === 404) {
            commit("setSnackbar", {
              content: "No room with the given room code exists",
              status: SnackbarStatus.ERROR,
            });
          } else {
            commit("setSnackbar", {
              content: "Could not edit room",
              status: SnackbarStatus.ERROR,
            });
          }
        }
        return false;
      }
    },
    /**
     * Calls backend at DELETE /rooms/${roomCode}
     * @param roomCode
     * @returns Promise<boolea>
     */
    async deleteRoom({ commit }, roomCode: string) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.delete(`/rooms/${roomCode}`);

        commit("setSnackbar", {
          content: "Room deleted",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error !== null) {
          if (error.response.status === 404) {
            commit("setSnackbar", {
              content: "No room with the given room code exists",
              status: SnackbarStatus.ERROR,
            });
          } else {
            commit("setSnackbar", {
              content: "Could not delete room",
              status: SnackbarStatus.ERROR,
            });
          }
        }
        return false;
      }
    },
    /**
     * Calls backend at GET /rooms/statistics/top-rooms
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @returns Promise<GETRoom[] | null>
     */
    async getTopRooms(
      { commit },
      editSnackbar?: boolean
    ): Promise<GETRoom[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/rooms/statistics/top-rooms");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find top rooms",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    /**
     * Calls backend at GET /sections/statistics/top-sections
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @returns Promise<GETSection[] | null>
     */
     async getTopSections(
      { commit },
      editSnackbar?: boolean
    ): Promise<GETSection[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/sections/statistics/top-sections");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find top sections",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    /**
     * Calls backend at GET /users/statistics/top-users
     * @param editSnackbar optional, if false, the snakcbar should only be edited when an error occurs
     * @returns Promise<GETRoom[] | null>
     */
     async getTopUsers(
      { commit },
      editSnackbar?: boolean
    ): Promise<User[] | null> {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/users/statistics/top-users");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find top users",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
  },
});

export function useStore() {
  return vuexUseStore(key);
}

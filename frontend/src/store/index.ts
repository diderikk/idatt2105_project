import User from "@/interfaces/User/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import SnackbarStatus from "../enum/SnackbarStatus.enum";
import backend from "../backend";
import CreateUser from "@/interfaces/User/User.interface";
import POSTReservation from "@/interfaces/Reservation/POSTReservation.interface";
import Reservation from "@/interfaces/Reservation/Reservation.interface";
import ReservationSorting from "@/interfaces/Reservation/ReservationSorting.interface";
import Room from "@/interfaces/Room/Room.interface";
import EditRoom from "@/interfaces/Room/EditRoom.interface";
import { UserToUserForm } from "@/utils/userUtils";
import TimeInterval from "@/interfaces/TimeInterval.interface";
import Message from "@/interfaces/Message.interface";

export interface State {
  user: string;
  token: string;
  snackbar: {
    content: string;
    status: SnackbarStatus;
  };
}

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
            phoneNationalCode: "",
            phoneNumber: "",
            isAdmin: false,
            expirationDate: "",
          }
        : JSON.parse(state.user),
    isUserLoggedIn: (state) => !!state.token,
    getSnackbar: (state) => state.snackbar,
  },
  actions: {
    async createUser({ commit, getters }, user: CreateUser): Promise<boolean> {
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
        commit("setSnackbar", {
          content: "Error could not log in",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
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
    logout({ commit }) {
      commit("setToken", "");
      commit("setUser", "");
      delete backend.defaults.headers["Authorization"];
    },
    async getUser({ commit }, userId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/users/${userId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not get user",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getUserStatistics({ commit }, userId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/users/${userId}/statistics`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not get user statistics",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getUsers({ commit }, editSnackbar?: boolean) {
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
    async getTopUsers({ commit }) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/users/statistics/top-users");
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        if (error !== null) {
          commit("setSnackbar", {
            content: "Could not get top users",
            status: SnackbarStatus.ERROR,
          });
        }
        return null;
      }
    },
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
        return false;
      }
    },
    async editReservation({ commit }, reservation: Reservation) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        if(!store.getters.getUser.isAdmin)
          await backend.post(`/users/${store.getters.getUser.userId}/reservations/${reservation.reservationId}`, reservation)
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
        commit("setSnackbar", {
          content: "Reservation could not be edited",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async deleteReservation({ commit }, reservationId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        if(!store.getters.getUser.isAdmin)
          await backend.delete(`/users/${store.getters.getUser.userId}/reservations/${reservationId}`)
        else
          await backend.delete(`/reservations/${reservationId}`);
        
        commit("setSnackbar", {
          content: "Reservation deleted",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          content: "Reservation could not be deleted",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async getReservation({ commit }, reservationId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        let response;
        if(!store.getters.getUser.isAdmin)
          response = await backend.get(`/users/${store.getters.getUser.userId}/reservations/${reservationId}`)
        else
          response = await backend.get(`/reservations/${reservationId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find a reservation",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getReservations({ commit }, editSnackbar: boolean, sortingConfig?: ReservationSorting) {
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
            //TODO add endpoint for sorting reservations for user
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
        commit("setSnackbar", {
          content: "Could not find any reservations",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getRooms({ commit }, editSnackbar?: boolean) {
      if (editSnackbar === undefined || editSnackbar === true)
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/rooms");
        if (editSnackbar === undefined || editSnackbar === true)
          commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find any rooms",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getAvailableRooms(
      { commit },
      timeInterval: { times: TimeInterval; reservationId?: number }
    ) {
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
          return null;
        }
      }
    },
    async getRoom({ commit }, roomCode: string) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/rooms/${roomCode}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find a room",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    /**
     * Gets messages that have been written in a room chat
     * @param param0 
     * @param roomCode 
     * @returns a list of messages
     */
    async getRoomMessages({ commit }, roomCode: string): Promise<Message[]>{
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try{
        const response = await backend.get(`/rooms/${roomCode}/messages`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          content: "Could not find a room",
          status: SnackbarStatus.ERROR,
        });
        return [];
      }
    },
    async createRoom({ commit }, room: Room) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post("/rooms", room);

        commit("setSnackbar", {
          content: "Reservation created",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
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
        return false;
      }
    },
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
    },
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
    },
  },
});

export function useStore() {
  return vuexUseStore(key);
}

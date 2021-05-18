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
      localStorage.setItem("token", state.token);
    },
    setUser(state, user) {
      state.user = JSON.stringify(user);
      localStorage.setItem("user", state.user);
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
          title: "Only admins can create users",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post("/users", user);
        commit("setSnackbar", {
          title: `User with email: ${user.email} created`,
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not create user",
          status: SnackbarStatus.ERROR,
        });
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
          title: "Error could not log in",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async deleteUser({ commit, getters }, user): Promise<boolean> {
      //Not letting users that aren't admins delete other users
      if (!getters.getUser.isAdmin) {
        commit("setSnackbar", {
          title: "Only admins can create users",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
      //Not wanting to be able to delete other admins
      if (user.isAdmin) {
        commit("setSnackbar", {
          title: "Cannot delete other admins",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.delete("/users", user);
        commit("setSnackbar", {
          title: `User with email: ${user.email} created`,
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not delete user",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    logout({ commit }) {
      commit("setToken", "");
      commit("setUser", "");
    },
    async getUser({ commit, getters }, userId: number) {
      const currentUser = getters.getUser;
      //Not letting users that aren't admins delete other users
      if (!currentUser.isAdmin && currentUser.userId !== userId) {
        commit("setSnackbar", {
          title: "Not access to get user",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/users/${userId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not get user",
          status: SnackbarStatus.ERROR,
        });
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
          title: "Reservation created",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        if (error.response.status === 400) {
          commit("setSnackbar", {
            title: "Already occupied",
            status: SnackbarStatus.ERROR,
          });
        } else {
          commit("setSnackbar", {
            title: "Could not create reservation",
            status: SnackbarStatus.ERROR,
          });
        }
        return false;
      }
    },
    async editReservation({ commit }, reservation: Reservation) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post(
          `/reservations/${reservation.reservationId}`,
          reservation
        );
        commit("setSnackbar", {
          title: "Reservation edited",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Reservation could not be edited",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async deleteReservation({ commit }, reservationId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.delete(`/reservations/${reservationId}`);
        commit("setSnackbar", {
          title: "Reservation deleted",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Reservation could not be deleted",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async getReservation({ commit }, reservationId: number) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get(`/reservations/${reservationId}`);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not find a reservation",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getReservations({ commit }, sortingConfig?: ReservationSorting) {
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

        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not find any reservations",
          status: SnackbarStatus.ERROR,
        });
        return null;
      }
    },
    async getRooms({ commit }) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const response = await backend.get("/rooms");
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return response.data;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not find any reservations",
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

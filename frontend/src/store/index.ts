import User from "@/interfaces/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import SnackbarStatus from "../enum/SnackbarStatus.enum";
import backend from "../backend";
import CreateUser from "@/interfaces/CreateUser.interface";
import CreateReservation from "@/interfaces/CreateReservation.interface";
import Reservation from "@/interfaces/Reservation.interface";
import ReservationSorting from "@/interfaces/ReservationSorting.interface";

export interface State {
  user: string;
  token: string;
  snackbar: {
    content: string;
    status: SnackbarStatus;
  };
}

export const key: InjectionKey<Store<State>> = Symbol();

export const store = createStore<State>({
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
    async createReservation(
      { commit, getters },
      reservation: CreateReservation
    ) {
      try {
        commit("setSnackbarStatus", SnackbarStatus.LOADING);
        await backend.post(`/users/${getters.getUser.id}/reservations`);
        commit("setSnackbar", {
          title: "Reservation created",
          status: SnackbarStatus.SUCCESS,
        });
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not create reservation",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
    async editReservation({ commit }, reservation: Reservation) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        const sections = reservation.sections.map((s: string) => {
          return { sectionName: s, roomCode: reservation.roomCode };
        });
        await backend.post(`/reservations/${reservation.id}`, {
          amountOfPeople: reservation.limit,
          startTime: reservation.startDate + " " + reservation.startTime,
          endTime: reservation.endDate + " " + reservation.endTime,
          reservationText: reservation.description,
          sections: sections,
        });
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
    async getReservations({ commit }, sortingConfig: ReservationSorting) {
      commit("setSnackbarStatus", SnackbarStatus.LOADING);
      try {
        await backend.post("/reservatios/sort", sortingConfig);
        commit("setSnackbarStatus", SnackbarStatus.NONE);
        return true;
      } catch (error) {
        commit("setSnackbar", {
          title: "Could not find any reservations",
          status: SnackbarStatus.ERROR,
        });
        return false;
      }
    },
  },
});

export function useStore() {
  return vuexUseStore(key);
}

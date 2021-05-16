import User from "@/interfaces/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import SnackBarStatus from "../enum/SnackbarStatus.enum";
import backend from "../backend";

export interface State {
  user: string;
  token: string;
  snackbar: {
    content: string;
    status: SnackBarStatus;
  };
}

export const key: InjectionKey<Store<State>> = Symbol();

export const store = createStore<State>({
  state: {
    user: localStorage.getItem("user") || "",
    token: localStorage.getItem("token") || "",
    snackbar: {
      content: "",
      status: SnackBarStatus.NONE,
    },
  },
  mutations: {
    setToken(state, token: string) {
      state.token = token;
      localStorage.setItem("token", state.token);
    },
    setUser(state, user) {
      state.user = user;
      localStorage.setItem("user", state.user);
    },
    setSnackbar(state, snackbar) {
      state.snackbar = snackbar;
    },
    setSnackbarStatus(state, status: SnackBarStatus) {
      state.snackbar.status = status;
    },
  },
  getters: {
    getUser: (state): User | Record<string, unknown> =>
      state.user === "" ? {} : JSON.parse(state.user),
    isUserLoggedIn: (state) => !!state.token,
    getSnackbar: (state) => state.snackbar,
  },
  actions: {
    async createUser({ commit, getters }): Promise<boolean> {
      //Not letting users that aren't admins create users
      if (!getters.getUser.isAdmin) return false;
      return true;
    },
    async login({ commit }, user: { email: string; password: string }) {
      commit("setSnackbarStatus", SnackBarStatus.LOADING);
      try {
        await backend.post("/login", user);
        commit("setSnackbarStatus", SnackBarStatus.NONE);
        return true;
      } catch (error) {
        console.log(error);
        commit("setSnackbar", {
          title: "Error could not log in",
          status: SnackBarStatus.ERROR,
        });
        return false;
      }
    },
    async deleteUser({ commit, getters }, user): Promise<boolean> {
      //Not letting users that aren't admins delete other users
      if (!getters.getUser.isAdmin) return false;
      //Not wanting to be able to delete other admins
      if (user.isAdmin) return false;
      return true;
    },
    logout({ commit }) {
      commit("setToken", "");
      commit("setUser", "");
    },
  },
});

export function useStore() {
  return vuexUseStore(key);
}

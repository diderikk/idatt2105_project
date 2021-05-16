import User from "@/interfaces/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";
import SnackBarStatus from "../enum/SnackbarStatus.enum";

export interface State {
  user: string;
  token: string;
  snackbar: {
    content: string,
    status: SnackBarStatus
  }
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
    async login() {
      //TODO change
      return true;
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
import { createStore } from "vuex";


export default createStore({
  state: {
    
  },
  mutations: {
    
  },
  actions: {},
  getters: {
  },
  modules: {},
});

export function useStore() {
  return vuexUseStore(key);
}

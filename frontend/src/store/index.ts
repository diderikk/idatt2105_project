import User from "@/interfaces/User.interface";
import { InjectionKey } from "vue";
import { createStore, Store, useStore as vuexUseStore } from "vuex";

export interface State {
  user: string;
  token: string;
  isLoading: false;
}

export const key: InjectionKey<Store<State>> = Symbol();

export const store = createStore<State>({
  state: {
    user: localStorage.getItem("user") || "",
    token: localStorage.getItem("token") || "",
    isLoading: false,
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
  },
  getters: {
    getUser: (state): User | Record<string, unknown> =>
      state.user === "" ? {} : JSON.parse(state.user),
    isUserLoggedIn: (state) => !!state.token,
    getIsLoading: (state) => state.isLoading,
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
  },
  modules: {},
});

export function useStore() {
  return vuexUseStore(key);
}

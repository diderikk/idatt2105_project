import { createStore } from "vuex";
import SnackBarStatus from "../enum/SnackbarStatus.enum";

export default createStore({
  state: {
    snackbar: {
      content: "",
      status: SnackBarStatus.NONE,
    },
  },
  mutations: {
    setSnackbar(state, snackbar) {
      state.snackbar = snackbar;
    },
  },
  actions: {},
  getters: {
    getSnackbar: (state) => state.snackbar,
  },
  modules: {},
});

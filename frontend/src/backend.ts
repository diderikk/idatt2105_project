import axios from "axios";
import SnackbarStatus from "./enum/SnackbarStatus.enum";
import router from "./router";
import { store } from "./store";

const isTesting = false;

export const URL = isTesting
  ? "https://localhost:8443/api/v1"
  : "https://bookthatroomserver.northeurope.cloudapp.azure.com:8443/api/v1";

const backend = axios.create({
  baseURL: URL,
});

const token = localStorage.getItem("token");
//Adds token to Authorization header if it is not null
if (token !== null) {
  backend.defaults.headers["Authorization"] =
    "Bearer " + localStorage.getItem("token");
}
//Telling axios to throw an error if the response status is not between 200 and 299
backend.defaults.validateStatus = (status: number) => {
  return status >= 200 && status < 300;
};

/**
 * When receiving a response from backend axios handles an error with status code 401 or 403 by logging the user out and sending an error message through the snackbar. It also returns Promis.reject(null) to tell all actions in store not to set the snackbar in their try catch, as to not override the snackbar set in this method
 * If an error without status code 401 or 403 happens it throws the error, so that the catch blocks in the vuex store actions can handle them
 */
backend.interceptors.response.use(undefined, (error) => {
  if (
    axios.isAxiosError(error) &&
    (error.response?.status === 401 || error.response?.status === 403)
  ) {
    store.dispatch("logout");
    store.commit("setSnackbar", {
      content: "You do not have permission to that area",
      status: SnackbarStatus.ERROR,
    });
    router.replace("/log-in");
    return Promise.reject(null);
  } else if (error) {
    throw error;
  }
});

export default backend;

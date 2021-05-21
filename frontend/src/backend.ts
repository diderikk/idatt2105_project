import axios from "axios";
import SnackbarStatus from "./enum/SnackbarStatus.enum";
import router from "./router";
import { store } from "./store";

const isTesting = false;
const backend = axios.create({
  baseURL: isTesting
    ? "https://localhost:8443/api/v1"
    : "https://bookthatroomserver.northeurope.cloudapp.azure.com:8443/api/v1",
});

const token = localStorage.getItem("token");
if (token !== null) {
  backend.defaults.headers["Authorization"] =
    "Bearer " + localStorage.getItem("token");
}
backend.defaults.validateStatus = (status: number) => {
  return status >= 200 && status < 300;
};

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

export const URL = isTesting ? "https://localhost:8443/api/v1" : "https://bookthatroomserver.northeurope.cloudapp.azure.com:8443/api/v1";

export default backend;

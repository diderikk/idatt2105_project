import axios from "axios";
import router from "./router";
import { useStore } from "./store";

const isTesting = false;
const backend = axios.create({
  baseURL: isTesting
    ? "https://localhost:8443/api/v1"
    : "https://40.87.146.194:8443/api/v1",
});

backend.defaults.headers.common["Authorization"] =
  "Bearer " + localStorage.getItem("token");
backend.defaults.validateStatus = (status: number) => {
  return status >= 200 && status < 300;
};

const store = useStore();
backend.interceptors.response.use(undefined, (error) => {
  if (
    error &&
    (error.config.status === 401 || error.config.status === 403) &&
    !error.config._retry
  ) {
    store.dispatch("logout");
    router.replace("/log-in");
  } else if (error) {
    throw error;
  }
});

export default backend;

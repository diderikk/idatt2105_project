import axios from "axios";
import router from "./router";
import { useStore } from "./store";

const isTesting = true;

const backend = axios.create({
  baseURL: isTesting ? "http://localhost:8080" : "https://13.69.249.213",
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

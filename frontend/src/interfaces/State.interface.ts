import SnackbarStatus from "@/enum/SnackbarStatus.enum";

export interface State {
  user: string;
  token: string;
  snackbar: {
    content: string;
    status: SnackbarStatus;
  };
}

export default State;

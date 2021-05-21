export const dateToString = (date: Date): string => {
  //Need to trick compilator
  let string = "" + date.getFullYear();
  const month = date.getMonth() + 1;
  string += "-";
  string += month < 10 ? "0" : "";
  string += month;
  const day = date.getDate();
  string += "-";
  string += day < 10 ? "0" : "";
  string += day;
  return string;
};

/**
 * Returns a date string as the backend server expects it
 * @param date 
 * @returns date string
 */
export const dateTimeToString = (date: Date): string => {
  let string = date.getHours() < 10 ? "0" : "";
  string += date.getHours();
  const minutes = date.getMinutes();
  string += ":";
  string += minutes < 10 ? "0" : "";
  string += minutes;
  return dateToString(date) + " " + string.substring(0,5);
}

export const removeTimeFromDate = (date: Date) => {
  return new Date(dateToString(date).split("T")[0]);
};

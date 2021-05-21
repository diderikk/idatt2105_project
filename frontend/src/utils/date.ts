/**
 * Converts a date to a string
 * @param date
 * @returns string
 */
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
  const seconds = date.getSeconds();
  string += ":";
  string += seconds < 10 ? "0" : "";
  string += seconds;
  return dateToString(date) + " " + string;
}

/**
 * Removes the time part of a date
 * @param date
 * @returns date where all time elements are set to 0
 */
export const removeTimeFromDate = (date: Date) => {
  return new Date(dateToString(date).split("T")[0]);
};

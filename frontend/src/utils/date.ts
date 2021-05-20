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
 * Removes the time part of a date
 * @param date
 * @returns date where all time elements are set to 0
 */
export const removeTimeFromDate = (date: Date) => {
  return new Date(dateToString(date).split("T")[0]);
};

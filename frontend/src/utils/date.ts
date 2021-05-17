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

export const removeTimeFromDate = (date: Date) => {
  return new Date(dateToString(date).split("T")[0]);
};

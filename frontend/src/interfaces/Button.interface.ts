export default interface Button {
  title: string;
  action: {
    function: () => {};
    //Either it takes checks, statuses and registerInformation, or only registerInformation
    numberOfArgs: 1 | 3;
  };
  class: string;
}

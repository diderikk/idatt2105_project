export default interface Button {
  title: string;
  action: {
    function: () => {};
    numberOfArgs: number;
  };
  class: string;
}

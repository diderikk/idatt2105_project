export default interface Button {
  title: string;
  action: {
    function: () => {};
    numberOfArgs: 1 | 3 | 3;
  };
  class: string;
}

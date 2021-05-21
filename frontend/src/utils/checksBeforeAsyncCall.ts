import InputFieldFeedbackStatus from "@/enum/InputFieldFeedbackStatus.enum";
import { Ref } from "vue";

/**
 * Checks if all chekcs are successful.
 * Runs through all checks to be able to give a user feedback for every check and not only one at a time.
 * @param checks a list of functions to be ran (which mutate statuses)
 * @param statuses a list of statuses to be checked if is successful
 * @returns
 */
const checksBeforeAsyncCall = (
  checks: Array<() => void>,
  statuses: Array<Ref<InputFieldFeedbackStatus>>
): boolean => {
  checks.forEach((check) => {
    check();
  });
  let numberOfPassedStatuses = 0;
  statuses.forEach((status) => {
    if (status.value === InputFieldFeedbackStatus.SUCCESS)
      numberOfPassedStatuses++;
  });
  return numberOfPassedStatuses === statuses.length;
};

export default checksBeforeAsyncCall;

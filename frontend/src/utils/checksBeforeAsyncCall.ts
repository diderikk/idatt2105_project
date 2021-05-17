import InputFieldFeedbackStatus from "@/enum/InputFieldFeedbackStatus.enum";
import { Ref } from "vue";

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

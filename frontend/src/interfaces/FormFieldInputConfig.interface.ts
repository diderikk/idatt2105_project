import InputFieldFeedbackStatus from "@/enum/InputFieldFeedbackStatus.enum";

export default interface FormFieldInputConfig {
  title: string;
  successHelperMessage?: string;
  errorHelperMessage: string;
  feedbackStatus: InputFieldFeedbackStatus;
}

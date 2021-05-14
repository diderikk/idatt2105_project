<template>
  <div>
    <label>{{ config.title }}</label>
    <slot id="input"></slot>
    <p v-if="config.feedbackStatus === SUCCESS">
      {{ config.successHelperMessage }}
    </p>
    <p v-else-if="config.feedbackStatus === ERROR">
      {{ config.errorHelperMessage }}
    </p>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import FormFieldInputConfig from "../interfaces/FormFieldInputConfig.interface";
export default defineComponent({
  name: "BaseFormFieldComponent",
  props: {
    config: {
      type: Object as PropType<FormFieldInputConfig>,
      required: true,
    },
  },
  setup() {
    //Have to extract from enum to be able to use them
    const SUCCESS = InputFieldFeedbackStatus.SUCCESS;
    const ERROR = InputFieldFeedbackStatus.ERROR;

    return {
      SUCCESS,
      ERROR,
    };
  },
});
</script>

<style scoped>
#input {
  display: block;
}
</style>

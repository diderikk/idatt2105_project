<template>
  <div class="field">
    <label v-if="'title' in config" class="label">{{ config.title }}</label>
    <slot></slot>
    <p v-if="isSuccess" class="help is-success">
      <span v-if="'successHelperMessage' in config">{{
        config.successHelperMessage
      }}</span
      ><span v-else>&#10003;</span>
    </p>
    <p v-else-if="isError" class="help is-danger">
      {{ config.errorHelperMessage }}
    </p>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from "vue";
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
  setup(props) {
    const isSuccess = computed(
      () => props.config.feedbackStatus === InputFieldFeedbackStatus.SUCCESS
    );

    const isError = computed(
      () => props.config.feedbackStatus === InputFieldFeedbackStatus.ERROR
    );

    return {
      isSuccess,
      isError,
    };
  },
});
</script>

<style scoped>
p {
  font-size: 15px;
}
</style>

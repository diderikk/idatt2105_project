<template>
  <div>
    <base-user-form-config :config="config"></base-user-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref } from "vue";
import BaseUserFormConfig from "../components/BaseUserForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import CreateUser from "../interfaces/CreateUser.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
export default defineComponent({
  name: "EditUser",
  components: {
    BaseUserFormConfig,
  },
  setup() {
    const register = (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerForm: CreateUser
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        //TODO Add async call and remove content
        console.log("Created user: REMOVE ME");
        console.log(registerForm);
      }
    };

    const config = {
      title: "Create User",
      buttons: [
        {
          title: "Create user",
          class: "button is-link is-primary",
          action: { function: register, numberOfArgs: 3 },
        },
      ],
    };

    return {
      register,
      config,
    };
  },
});
</script>

<style scoped></style>

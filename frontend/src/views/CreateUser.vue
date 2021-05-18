<template>
  <div>
    <base-user-form-config :config="config"></base-user-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref } from "vue";
import BaseUserFormConfig from "../components/BaseUserForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import UserForm from "../interfaces/User/UserForm.interface";
import { useStore } from "../store";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import { UserFormToUser } from "../utils/userUtils";
export default defineComponent({
  name: "CreateUser",
  components: {
    BaseUserFormConfig,
  },
  setup() {
    const store = useStore();
    const register = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerForm: UserForm
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        await store.dispatch("createUser", UserFormToUser(registerForm));
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

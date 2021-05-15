<template>
  <div>
    <base-user-form-config
      :baseUser="user"
      :config="config"
    ></base-user-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, Ref } from "vue";
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
    //TODO remove testdata and do async call for actual user
    const user: CreateUser = reactive({
      firstName: "Kalle",
      lastName: "Kaviar",
      email: "kalle.kaviar@kaviar.com",
      phoneNationalCode: "47",
      phoneNumber: "12345678",
      expirationDate: "2022-02-03",
    });

    const editProfile = (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: CreateUser
    ) => {
      console.log(checks);
      console.log(statuses);
      if (checksBeforeAsyncCall(checks, statuses)) {
        //TODO Add async call and remove content
        console.log("Edited user: REMOVE ME");
        console.log(registerInformation);
      }
    };

    const deleteProfile = (registerInformation: CreateUser) => {
      if (window.confirm("Are you sure you want do delete the user?")) {
        //TODO Add async call
        console.log("User deleted: REMOVE ME");
        console.log(registerInformation);
      }
    };

    const config = {
      title: "Edit user",
      buttons: [
        {
          title: "Confirm edit",
          class: "button is-link is-primary",
          action: { function: editProfile, numberOfArgs: 3 },
        },
        {
          title: "Delete user",
          class: "button is-danger",
          action: {
            function: deleteProfile,
            numberOfArgs: 1,
          },
        },
      ],
    };

    return {
      user,
      config,
      editProfile,
      deleteProfile,
    };
  },
});
</script>
<style scoped></style>

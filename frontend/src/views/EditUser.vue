<template>
  <div>
    <base-user-form-config
      v-if="isDoneLoading"
      :baseUser="user"
      :userId="parseInt(id)"
      :config="config"
    ></base-user-form-config>
    <base-user-form-config
      v-else
      :baseUser="user"
      :config="config"
    ></base-user-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, Ref } from "vue";
import { useRouter } from "vue-router";
import BaseUserFormConfig from "../components/BaseUserForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import UserForm from "../interfaces/User/UserForm.interface";
import { store } from "../store";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import { UserFormToUser, UserToUserForm } from "../utils/userUtils";
export default defineComponent({
  name: "EditUser",
  components: {
    BaseUserFormConfig,
  },
  props: {
    id: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const router = useRouter();

    const user: Ref<UserForm> = ref({
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      expirationDate: "",
      isAdmin: false,
    });
    const isDoneLoading = ref(false);

    onMounted(async () => {
      const response = await store.dispatch("getUser", props.id);
      if (response !== null) {
        user.value = UserToUserForm(response);
        isDoneLoading.value = true;
      }
    });

    const editProfile = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: UserForm,
      userId: number
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        await store.dispatch(
          "editUser",
          UserFormToUser(registerInformation, userId)
        );
      }
    };

    const deleteProfile = async (userId: number) => {
      if (window.confirm("Are you sure you want to delete the user?")) {
        if (await store.dispatch("deleteUser", userId)) {
          router.push("/users");
        }
      }
    };

    /**
     * The config object to be sent to BaseUserForm, containing title, and buttons
     */
    const config = {
      title: "Edit user",
      buttons: [
        {
          title: "Confirm edit",
          class: "button is-link is-primary",
          action: { function: editProfile, numberOfArgs: 4 },
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
      isDoneLoading,
    };
  },
});
</script>
<style scoped></style>

<template>
  <div class="card">
    <div class="card-header">
      <p class="card-header-title">{{ user.firstName }} {{ user.lastName }}</p>
      <span v-if="user.isAdmin" class="tag is-dark">Admin</span>
    </div>
    <div class="card-content">
      <label class="label">Email</label>
      <p>{{ user.email }}</p>
      <label class="label">Phone number</label>
      <p>{{ user.phoneNumber }}</p>
      <label class="label">Expiration date</label>
      <p v-if="user.expirationDate === null">None</p>
      <p v-else>{{ user.expirationDate }}</p>
    </div>
    <div class="card-footer">
      <router-link :to="`/users/${user.userId}`" class="card-footer-item"
        >View</router-link
      >
      <router-link :to="`/edit-user/${user.userId}`" class="card-footer-item"
        >Edit</router-link
      >
      <a @click="deleteUser" class="card-footer-item">Delete</a>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import User from "../interfaces/User/User.interface";
import { useStore } from "../store";
export default defineComponent({
  name: "UserCard",
  props: {
    user: {
      required: true,
      type: Object as () => User,
    },
  },
  setup(props, { emit }) {
    const store = useStore();
<<<<<<< HEAD

=======
    const view = () => {
      router.push(`/users/${props.user.userId}`);
    };
    const edit = () => {
      router.push(`/edit-user/${props.user.userId}`);
    };
>>>>>>> dev
    const deleteUser = async () => {
      if (window.confirm("Are you sure you want do delete the user?")) {
        if (await store.dispatch("deleteUser", props.user.userId)) {
          emit("reload");
        }
      }
    };

    return {
<<<<<<< HEAD
=======
      view,
      edit,
>>>>>>> dev
      deleteUser,
    };
  },
});
</script>

<style scoped></style>

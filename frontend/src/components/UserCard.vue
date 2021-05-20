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
      <a @click="view" href="#" class="card-footer-item">View</a>
      <a @click="edit" href="#" class="card-footer-item">Edit</a>
      <a @click="deleteUser" href="#" class="card-footer-item">Delete</a>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
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
    const router = useRouter();
    const store = useStore();
    const view = () => {
      router.push(`/users/${props.user.userId}`);
    };
    const edit = () => {
      router.push(`/edit-user/${props.user.userId}`);
    };
    const deleteUser = async () => {
      if (window.confirm("Are you sure you want do delete the user?")) {
        if (await store.dispatch("deleteUser", props.user.userId)) {
          emit("reload");
        }
      }
    };

    return {
      view,
      edit,
      deleteUser,
    };
  },
});
</script>

<style scoped></style>

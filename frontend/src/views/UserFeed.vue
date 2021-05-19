<template>
  <div>
    <input class="input" type="text" placeholder="Search" />
    <div v-if="users.length === 0" class="box">No users available</div>
    <span v-else
      ><user-card v-for="(user, index) in users" :key="index" :user="user">{{
        user
      }}</user-card></span
    >
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import UserCard from "../components/UserCard.vue";
import User from "../interfaces/User/User.interface";
import { useStore } from "../store";
export default defineComponent({
  name: "UserFeed",
  components: {
    UserCard,
  },
  setup() {
    const store = useStore();
    const users = ref([] as User[]);
    onMounted(async () => {
      const response = await store.dispatch("getUsers");
      if (response !== null) {
        users.value = response;
      }
    });

    return {
      users,
    };
  },
});
</script>

<style scoped>
div {
  margin: 20px 0px;
}
</style>

<template>
  <div>
    <input
      v-model="searchInput"
      class="input"
      type="text"
      placeholder="Search"
    />
    <div v-if="users.length === 0" class="box">No users available</div>
    <span v-else
      ><user-card
        v-for="(user, index) in availableUsers"
        :key="index"
        :user="user"
        @reload="reload(false)"
        >{{ user }}</user-card
      ></span
    >
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
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
    const searchInput = ref("");
    onMounted(async () => {
      await reload(true);
    });

    const reload = async (editSnackbar: boolean) => {
      const response = await store.dispatch("getUsers", editSnackbar);
      if (response !== null) {
        users.value = response;
      }
    };

    const availableUsers = computed(() => {
      return users.value.filter((user) => compareUserValues(user));
    });

    const compareUserValues = (user: User) => {
      const searchLowerCase = searchInput.value.toLowerCase();
      return (
        user.firstName.toLowerCase().startsWith(searchLowerCase) ||
        user.lastName.toLowerCase().startsWith(searchLowerCase) ||
        user.email.toLowerCase().startsWith(searchLowerCase) ||
        user.phoneNumber.toLowerCase().startsWith(searchLowerCase)
      );
    };

    return {
      searchInput,
      users,
      reload,
      availableUsers,
    };
  },
});
</script>

<style scoped>
div {
  margin: 20px 0px;
}
</style>

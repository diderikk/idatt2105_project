<template>
  <div>
    <base-feed-header
      :createRoute="'/create-user'"
      @inputChange="changeInput($event, input)"
    ></base-feed-header>
    <div v-if="users.length === 0" class="box" id="placeholder">
      No users available
    </div>
    <div v-else class="columns is-multiline">
      <div
        v-for="(user, index) in availableUsers"
        :key="index"
        class="column is-half"
      >
        <user-card :user="user" @reload="reload(false)"></user-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import BaseFeedHeader from "../components/BaseFeedHeader.vue";
import UserCard from "../components/UserCard.vue";
import User from "../interfaces/User/User.interface";
import { useStore } from "../store";
export default defineComponent({
  name: "UserFeed",
  components: {
    UserCard,
    BaseFeedHeader,
  },
  setup() {
    const store = useStore();
    const users = ref([] as User[]);
    const searchInput = ref("");
    const changeInput = (input: string) => {
      searchInput.value = input;
    };

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
      changeInput,
      users,
      reload,
      availableUsers,
    };
  },
});
</script>

<style scoped>
#placeholder {
  margin: 25px 0px;
}
</style>

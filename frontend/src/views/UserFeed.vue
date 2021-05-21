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

    /**
     * Gets the users to be displayed, while also displaying the loading animation with the snackbar
     */
    onMounted(async () => {
      await reload(true);
    });

    /**
     * Reloads all users
     * @editSnackbar defines if the snackbar should be changed when loading the accounts. When an account has been deleted the user should receive feedback from the delete action that the user has been deleted, and therefore editSnackbar has to be false to not override the previous feedback.
     */
    const reload = async (editSnackbar: boolean) => {
      const response = await store.dispatch("getUsers", editSnackbar);
      if (response !== null) {
        users.value = response;
      }
    };

    /**
     * Computes the availible user based on the filter from the compareUserValues method.
     */
    const availableUsers = computed(() => {
      return users.value.filter((user) => compareUserValues(user));
    });

    /**
     * Finds out if the searchInput matches with the start of a users first name, last name, email or phone number.
     * Ignores caps
     */
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

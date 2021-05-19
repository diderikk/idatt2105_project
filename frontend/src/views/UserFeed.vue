<template>
  <div>
    <div v-if="windowWidth > 768" class="columns">
      <div class="column is-one-quarter">
        <button @click="addNewUser" class="button is-link is-primary">
          + Create
        </button>
      </div>
      <div class="column"></div>
      <div class="column is-two-thirds">
        <input
          v-model="searchInput"
          class="input"
          type="text"
          placeholder="Search"
        />
      </div>
    </div>
    <span v-else>
      <div class="columns">
        <div class="column">
          <input
            v-model="searchInput"
            class="input"
            type="text"
            placeholder="Search"
          />
        </div>
      </div>
      <button id="add-button" class="button is-link is-primary">+</button>
    </span>
    <div v-if="users.length === 0" class="box">No users available</div>
    <div v-else class="columns">
      <div
        v-for="(user, index) in availableUsers"
        :key="index"
        class="column is-one-third"
      >
        <user-card :user="user" @reload="reload(false)">{{ user }}</user-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  onBeforeUnmount,
  onMounted,
  ref,
} from "vue";
import { useRouter } from "vue-router";
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
    const router = useRouter();
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

    const addNewUser = () => {
      router.push("/create-user");
    };

    const windowWidth = ref(window.innerWidth);
    const onResize = () => {
      //768
      console.log(windowWidth.value);
      windowWidth.value = window.innerWidth;
    };
    window.addEventListener("resize", onResize);
    onBeforeUnmount(() => {
      window.removeEventListener("resize", onResize);
    });
    return {
      searchInput,
      users,
      reload,
      availableUsers,
      addNewUser,
      windowWidth,
    };
  },
});
</script>

<style scoped>
#add-button {
  position: fixed;
  bottom: 100px;
  right: 10px;
  z-index: 100;
  border-radius: 50%;
  border: none;
}
</style>

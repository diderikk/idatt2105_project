<template>
  <div>
    <h1 class="title">Log in</h1>
    <div class="field">
      <label class="label">Email</label>
      <input v-model="email" type="text" alt="email" class="input" />
    </div>

    <div class="field">
      <label class="label">Password</label>
      <input v-model="password" type="password" alt="password" class="input" />
    </div>
    <button @click="logIn" class="button is-link is-primary">Log in</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "../store";
export default defineComponent({
  name: "LogIn",
  setup() {
    const userInfo = reactive({
      email: "",
      password: "",
    });

    const store = useStore();
    const router = useRouter();

    const logIn = async () => {
      if (await store.dispatch("login", userInfo)) router.push("/reservations");
    };

    return {
      ...toRefs(userInfo),
      logIn,
    };
  },
});
</script>

<style scoped></style>

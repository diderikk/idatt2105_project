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
export default defineComponent({
  name: "LogIn",
  setup() {
    const userInfo = reactive({
      email: "",
      password: "",
    });

    const logIn = () => {
      //TODO remove console log and add axios call
      console.log(userInfo);
    };

    return {
      ...toRefs(userInfo),
      logIn,
    };
  },
});
</script>

<style scoped></style>

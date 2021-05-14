<template>
  <div>
    <h1>Log in</h1>
    <label>Email</label>
    <input v-model="email" type="text" alt="email" />
    <label>Password</label>
    <input v-model="password" type="password" alt="password" />
    <button @click="logIn">Log in</button>
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

<template>
  <div>
    <nav id="nav" class="navbar is-dark" role="navigation">
      <div class="navbar-start">
        <router-link to="/" class="navbar-item">Home</router-link>
        <router-link to="/about" class="navbar-item">About</router-link>
        <router-link class="navbar-item" to="/create-user"
          >Create new user</router-link
        >
        <router-link class="navbar-item" to="/create-reservation"
          >Make reservation</router-link
        >
      </div>
      <div class="navbar-end">
        <!--Could not wrap the items in a div and use on if-else since it broke Bulma -->

        <router-link class="navbar-item is-white" to="/log-in"
          >Log In</router-link
        >
        <router-link @click="signOut" class="navbar-item" to="/log-in">
          Sign out
        </router-link>
      </div>
    </nav>
    <div id="application-wrapper">
      <router-view />
    </div>
    <snackbar></snackbar>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Snackbar from "./components/Snackbar.vue";
export default defineComponent({
  name: "App",
  components: { Snackbar },
});
</script>

<style>
body {
  margin: 0px;
}

#application-wrapper {
  width: 60%;
  margin: auto;
  margin-top: 80px;
}

#nav {
  /*Wanted to use position sticky but many browsers does not support that*/
  position: fixed;
  width: 100%;
  top: 0px;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>

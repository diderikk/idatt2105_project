<template>
  <div>
    <nav id="nav" class="navbar is-dark" role="navigation">
      <div class="navbar-brand">
        <router-link to="/" class="navbar-item"
          ><img alt="Book that room logo" src="./assets/logo.png"
        /></router-link>
        <a
          :class="{ 'is-active': navBarIsActive }"
          @click="toggleNavBar"
          class="navbar-burger"
        >
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
        </a>
      </div>
      <div
        :class="{ 'is-active': navBarIsActive }"
        id="desktop-menu"
        class="navbar-menu"
      >
        <!--Could not wrap the items in a span and use if-else on the isLoggedIn property since it broke Bulma -->
        <div class="navbar-start">
          <router-link
            v-if="isLoggedIn"
            class="navbar-item"
            to="/reservations"
            >Reservations</router-link
          >
          <router-link v-if="isAdmin" class="navbar-item" to="/users"
            >Users</router-link
          >

          <router-link v-if="isLoggedIn" class="navbar-item" to="/rooms"
            >Rooms</router-link
          >
        </div>
        <div class="navbar-end">
          <router-link
            v-if="!isLoggedIn"
            class="navbar-item is-white"
            to="/log-in"
            >Log In</router-link
          >
          <router-link v-if="isLoggedIn" class="navbar-item" to="/users">
            Profile
          </router-link>
          <router-link
            v-if="isLoggedIn"
            @click="logout"
            class="navbar-item"
            to="/log-in"
          >
            Log out
          </router-link>
        </div>
      </div>
    </nav>
    <div id="application-wrapper" @click="closeNavBar">
      <router-view />
    </div>
    <snackbar @click="closeNavBar"></snackbar>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
import Snackbar from "./components/Snackbar.vue";
import { useStore } from "./store";
export default defineComponent({
  name: "App",
  components: { Snackbar },
  setup() {
    const router = useRouter();
    const store = useStore();

    const isLoggedIn = computed(() => store.getters.isUserLoggedIn);
    const isAdmin = computed(() => store.getters.getUser.isAdmin);

    const toggleNavBar = () => {
      navBarIsActive.value = !navBarIsActive.value;
    };

    router.afterEach((to, from, failure) => {
      if (to.fullPath !== from.fullPath) {
        navBarIsActive.value = false;
      }
    });

    const navBarIsActive = ref(false);

    const closeNavBar = () => {
      navBarIsActive.value = false;
    };

    const logout = () => {
      store.dispatch("logout");
      router.replace("/log-in");
    };

    return {
      logout,
      navBarIsActive,
      toggleNavBar,
      isAdmin,
      isLoggedIn,
      closeNavBar,
    };
  },
});
</script>

<style>
body {
  margin: 0px;
}

#application-wrapper {
  width: 60%;
  margin: auto;
  margin-top: 70px;
  margin-bottom: 10px;
}

@media only screen and (max-width: 1020px) {
  #application-wrapper {
    width: 95%;
    margin: auto;
    margin-top: 70px;
    margin-bottom: 10px;
  }
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

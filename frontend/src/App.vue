<template>
  <div>
    <nav class="navbar is-primary" role="navigation">
      <div class="navbar-start">
        <router-link to="/" class="navbar-item">Home</router-link>
        <router-link to="/about" class="navbar-item">About</router-link>
        <router-link class="navbar-item" to="/create-user"
          >Create new user</router-link
        >
        <router-link class="navbar-item" to="/edit-user">Edit user</router-link>
        <router-link class="navbar-item" to="/create-reservation"
          >Make reservation</router-link
        >
        <router-link class="navbar-item" to="/edit-reservation"
          >Edit reservation</router-link
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
  </div>
</template>

<style>
body {
  margin: 0px;
}

#application-wrapper {
  width: 60%;
  margin: 4vh auto;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>

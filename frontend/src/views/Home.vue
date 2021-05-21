<template>
  <div class="home">
    <div class="columns">
      <div class="column is-half has-text-justified">
        <h1 class="title">Book That Room</h1>
        <p class="subtitle">The number 1 place to book rooms</p>
      </div>
      <div class="column is-one-quarter"></div>
      <div class="column is-one-quarter">
        <!--<figure class="image is-1by1 is-rounded">-->
        <img alt="Book that room logo" src="../assets/logo.png" class="" />
        <!--</figure>-->
      </div>
    </div>
    <div class="columns">
      <div class="column is-two-thirds">
        <h2 class="title is-4">About us</h2>
        <p class="subtitle is-5">
          <strong>Book That Room</strong> is a solution striving to make
          reserving rooms as easy as possible. Lorem ipsum dolor sit amet
          consectetur adipisicing elit. Pariatur aliquam, odit rerum impedit
          voluptatem a delectus nihil veritatis facere ullam aspernatur ducimus
          magni dignissimos ea nostrum dolores saepe, ipsum odio rem nam veniam
          consectetur modi. Earum cumque deserunt doloribus dolorum et voluptate
          eum neque ratione. Enim blanditiis impedit corrupti officiis. Magni,
          iusto! Magnam quae ipsum quod incidunt error consectetur, earum
          exercitationem ducimus aspernatur asperiores. Exercitationem
          asperiores inventore tenetur debitis consequuntur fugiat quae iure,
          accusamus, nesciunt ducimus voluptas sunt fuga ipsam aspernatur error
          eum nisi, adipisci recusandae dolore dignissimos labore. Suscipit modi
          quis autem vitae, quidem atque dolores sunt maiores expedita?
        </p>
      </div>
      <div class="column is-one-thirds is-dark">
        <div class="box is-dark">
          <h2 class="title is-5">Quick facts</h2>
          <ul>
            <li>Created in 2021.</li>
            <li>Made by Rokas Bliudzius, Martin Hagen and Diderik Kramer.</li>
            <li>Aims to revolutionize the room booking service.</li>
          </ul>
        </div>
      </div>
    </div>
    <div class="has-text-centered">
      <router-link v-if="isLoggedIn" to="/reservations" class="button is-link"
        >View reservations</router-link
      >
      <router-link v-if="!isLoggedIn" to="/log-in" class="button is-link"
        >Log in</router-link
      >
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from "vue";
import { useStore } from "../store";

export default defineComponent({
  name: "Home",
  components: {},
  setup() {
    const store = useStore();
    const isLoggedIn = computed(() => store.getters.isUserLoggedIn);

    return {
      isLoggedIn,
    };
  },
});
</script>

<style scoped></style>

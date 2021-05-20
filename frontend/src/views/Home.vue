<template>
  <div class="home">
    <section class="hero">
      <div class="hero-body">
        <p class="title">Book That Room</p>
        <p class="subtitle">The number 1. place to book rooms</p>
      </div>
    </section>
    <div id="image-container">
      <img alt="Book that room logo" src="../assets/logo.png" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "Home",
  components: {},
});
</script>

<style scoped>
#image-container {
  margin: auto;
  display: flex;
  justify-content: center;
}
</style>

<template>
    <div class="container">
      <div class="columns is-vcentered">
        <div class="column has-text-centered">
          <h1 class="title">404</h1>
          <h2 class="title">Page Not Found</h2>
          <p class="subtitle">An unexpected error has occurred</p>
        </div>
      </div>
    </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
  name: "PageNotFound",
});
</script>

<style scoped>
h1 {
  font-size: 150px;
}

.container {
  margin-top: 40vh;
}
</style>

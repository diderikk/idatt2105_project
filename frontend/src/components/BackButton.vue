<template>
  <div>
    <a @click="goBack" href="#" class="link">&#8592;</a>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
export default defineComponent({
  name: "BackButton",
  setup() {
    const router = useRouter();
    const goBack = () => {
      router.back();
    };

    return {
      goBack,
    };
  },
});
</script>

<style scoped></style>

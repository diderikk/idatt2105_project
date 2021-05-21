<template>
  <div>
    <a @click="goBack" class="link">&#8592;</a>
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
      router.go(-1);
    };

    return {
      goBack,
    };
  },
});
</script>

<style scoped>
a {
  font-size: 30px;
}
</style>

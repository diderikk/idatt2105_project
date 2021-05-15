<template>
  <div
    id="snack-bar"
    :style="{ visibility: isVisible }"
    class="notification is-dark"
  >
    <button @click="close" class="delete"></button>
    <pulse-loader
      v-if="isLoading"
      :loading="isLoading"
      color="white"
    ></pulse-loader>
    <p v-else :class="{ error: isError, success: isSuccess }">
      {{ snackbar.title }}
    </p>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
//TODO Change useStore import to the one defined in authentication branch
import { useStore } from "vuex";
import SnackBarStatus from "../enum/SnackbarStatus.enum";
import PulseLoader from "vue-spinner/src/PulseLoader.vue";
export default defineComponent({
  name: "Snackbar",
  components: {
    PulseLoader,
  },
  setup() {
    const store = useStore();

    const visible = ref(true);
    const isVisible = computed(() => {
      return visible.value ? "visible" : "hidden";
    });

    const snackbar = computed(() => {
      return store.getters.getSnackbar;
    });

    store.commit("setSnackbar", {
      title: "Loading",
      status: SnackBarStatus.LOADING,
    });

    setTimeout(() => {
      store.commit("setSnackbar", {
        title: "Error",
        status: SnackBarStatus.ERROR,
      });
    }, 3000);

    watch(
      () => snackbar.value.status,
      () => {
        visible.value = true;
        setTimeout(() => {
          visible.value = false;
        }, 7000);
      }
    );

    const isLoading = computed(
      () => snackbar.value.status === SnackBarStatus.LOADING
    );

    const isError = computed(
      () => snackbar.value.status === SnackBarStatus.ERROR
    );

    const isSuccess = computed(
      () => snackbar.value.status === SnackBarStatus.SUCCESS
    );

    const close = () => {
      visible.value = false;
    };
    return {
      isVisible,
      close,
      snackbar,
      isError,
      isSuccess,
      isLoading,
    };
  },
});
</script>

<style scoped>
#snack-bar {
  width: 65%;
  bottom: 10px;
  left: 50%;
  transform: translate(-50%, 0);
  position: fixed;
}

.success {
  color: hsl(141, 53%, 53%);
}

.error {
  color: hsl(348, 100%, 61%);
}
</style>

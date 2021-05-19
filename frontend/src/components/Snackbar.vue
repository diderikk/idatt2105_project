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
      {{ snackbar.content }}
    </p>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
//TODO Change useStore import to the one defined in authentication branch
import { useStore } from "../store";
import SnackbarStatus from "../enum/SnackbarStatus.enum";
import PulseLoader from "vue-spinner/src/PulseLoader.vue";
export default defineComponent({
  name: "Snackbar",
  components: {
    PulseLoader,
  },
  setup() {
    const store = useStore();
    const visible = ref(false);

    /**
     * Computes the css value to be added to the css visibility attribute
     */
    const isVisible = computed(() => {
      return visible.value ? "visible" : "hidden";
    });

    const snackbar = computed(() => {
      return store.getters.getSnackbar;
    });

    /**
     * Watches the snackbar status, if it is changed to anything other than none, it displays the snackbar, and removes it after seven seconds, if it is changed to NONE its closed immediately
     */
    watch(
      () => snackbar.value.status,
      () => {
        if (snackbar.value.status === SnackbarStatus.NONE) {
          visible.value = false;
        } else if (snackbar.value.status === SnackbarStatus.LOADING) {
          visible.value = true;
        } else {
          visible.value = true;
          setTimeout(() => {
            visible.value = false;
          }, 7000);
        }
      }
    );

    /**
     * Checks if the snackbar status is loading
     */
    const isLoading = computed(
      () => snackbar.value.status === SnackbarStatus.LOADING
    );

    /**
     * Checks if the snackbar status is error
     */
    const isError = computed(
      () => snackbar.value.status === SnackbarStatus.ERROR
    );

    /**
     * Checks if the snackbar status is success
     */
    const isSuccess = computed(
      () => snackbar.value.status === SnackbarStatus.SUCCESS
    );

    /**
     * Closes the snackbar
     */
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

p {
  font-size: 17.5px;
}
</style>

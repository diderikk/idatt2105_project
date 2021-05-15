<template>
  <div
    id="snack-bar"
    :style="{ visibility: isVisible }"
    class="notification is-dark"
  >
    <button @click="close" class="delete"></button>
    <p :class="{ error: isError, success: isSuccess }">{{ snackbar.title }}</p>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
//TODO Change useStore import to the one defined in authentication branch
import { useStore } from "vuex";
import SnackBarStatus from "../enum/SnackbarStatus.enum";
export default defineComponent({
  name: "Snackbar",
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
        title: "Success",
        status: SnackBarStatus.SUCCESS,
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

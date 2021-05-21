<template>
  <div>
    <div v-if="windowWidth > 768" class="columns">
      <div v-if="!isAdminAndAdminNeeded" class="column is-one-quarter">
        <button @click="toCreate" class="button is-link is-primary">
          + Create
        </button>
      </div>
      <div v-if="!isAdminAndAdminNeeded" class="column"></div>
      <div :class="{ 'is-two-thirds': !isAdminAndAdminNeeded }" class="column">
        <input
          v-model="searchInput"
          class="input"
          type="text"
          placeholder="Search"
        />
      </div>
    </div>
    <span v-else>
      <div class="columns">
        <div class="column">
          <input
            v-model="searchInput"
            class="input"
            type="text"
            placeholder="Search"
          />
        </div>
      </div>

      <button
        v-if="!isAdminAndAdminNeeded"
        @click="toCreate"
        id="add-button"
        class="button is-link is-primary"
      >
        +
      </button>
    </span>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onBeforeUnmount, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "../store";
export default defineComponent({
  name: "BaseFeedHeader",
  props: {
    createRoute: {
      required: true,
      type: String,
    },
    needsToBeAdmin: {
      required: true,
      type: Boolean,
    },
  },
  setup(props, { emit }) {
    const router = useRouter();
    const store = useStore();
    const searchInput = ref("");
    watch(
      () => searchInput.value,
      () => {
        emit("inputChange", searchInput.value);
      }
    );

    const toCreate = () => {
      router.push(props.createRoute);
    };

    const windowWidth = ref(window.innerWidth);
    const onResize = () => {
      windowWidth.value = window.innerWidth;
    };
    window.addEventListener("resize", onResize);
    onBeforeUnmount(() => {
      window.removeEventListener("resize", onResize);
    });

    const isAdminAndAdminNeeded = computed(
      () => !store.getters.getUser.isAdmin && props.needsToBeAdmin
    );

    return {
      searchInput,
      toCreate,
      windowWidth,
      isAdminAndAdminNeeded,
    };
  },
});
</script>

<style scoped>
#add-button {
  position: fixed;
  bottom: 100px;
  right: 10px;
  z-index: 100;
  border-radius: 50%;
  border: none;
}
</style>

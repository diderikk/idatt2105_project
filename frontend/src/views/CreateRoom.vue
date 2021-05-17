<template>
    <base-room-form-config :config="config"></base-room-form-config>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import BaseRoomFormConfig from "../components/BaseRoomForm.vue";
export default defineComponent({
  name: "CreateRoom",
  components: { BaseRoomFormConfig },
  setup() {
    const config = {
      title: "Create Room",
      buttons: [

      ],
    };

    return{
        config
    }
  },
});
</script>

<style scoped></style>

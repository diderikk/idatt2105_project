<template>
  <base-room-form-config :config="config"></base-room-form-config>
</template>

<script lang="ts">
import { defineComponent, Ref } from "vue";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import BaseRoomFormConfig from "../components/BaseRoomForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import { useStore } from "../store";
import { RoomFormToRoom } from "../utils/roomUtils"
import RoomForm from "../interfaces/Room/RoomForm.interface";
export default defineComponent({
  name: "CreateRoom",
  components: { BaseRoomFormConfig },
  setup() {
    const store = useStore();
    const register = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      roomForm: RoomForm
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        store.dispatch("createRoom", RoomFormToRoom(roomForm));
      }
    };

    const config = {
      title: "Create Room",
      buttons: [
        {
          title: "Create room",
          class: "button is-link is-primary",
          action: { function: register, numberOfArgs: 3 },
        },
      ],
    };

    return {
      config,
    };
  },
});
</script>

<style scoped></style>

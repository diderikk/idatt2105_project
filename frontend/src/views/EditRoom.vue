<template>
  <base-room-form-config
    v-if="isDoneLoading"
    :baseRoom="room"
    :config="config"
  ></base-room-form-config>
  <base-room-form-config
    v-else
    :baseRoom="room"
    :config="config"
  ></base-room-form-config>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, Ref } from "vue";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import BaseRoomFormConfig from "../components/BaseRoomForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import { useStore } from "../store";
import { useRouter } from "vue-router";
import { RoomFormToRoom, RoomToRoomForm } from "../utils/roomUtils";
import RoomForm from "../interfaces/Room/RoomForm.interface";
export default defineComponent({
  name: "EditRoom",
  components: { BaseRoomFormConfig },
  props: {
    code: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const store = useStore();
    const router = useRouter();
    const isDoneLoading = ref(false);
    const room: Ref<RoomForm> = ref({
      roomCode: "",
      sections: [],
    });

    onMounted(async () => {
      const response = await store.dispatch("getRoom", props.code);
      if (response !== null) {
        room.value = RoomToRoomForm(response);
        isDoneLoading.value = true;
      }
    });

    const editRoom = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      roomForm: RoomForm
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        await store.dispatch("editRoom", {
          ...RoomFormToRoom(roomForm),
          originalRoomCode: props.code,
        });
        if (roomForm.roomCode !== props.code)
          router.push(`/edit-room/${roomForm.roomCode}`);
      }
    };

    const deleteRoom = async () => {
      if (window.confirm("Are you sure you want do delete the room?")) {
        await store.dispatch("deleteRoom", props.code);
      }
    };

    /**
     * The config object to be sent to BaseRoomForm, containing title, and buttons
     */
    const config = {
      title: "Edit Room",
      buttons: [
        {
          title: "Confirm edit",
          class: "button is-link is-primary",
          action: { function: editRoom, numberOfArgs: 3 },
        },
        {
          title: "Delete room",
          class: "button is-danger margin-left",
          action: {
            function: deleteRoom,
            numberOfArgs: 0,
          },
        },
      ],
    };

    return {
      room,
      isDoneLoading,
      config,
    };
  },
});
</script>

<style scoped></style>

<template>
  <div class="card">
    <div class="card-header">
      <p class="card-header-title">{{ room.roomCode }}</p>
    </div>
    <div class="card-content">
      <label class="label">Sections:</label>
      <ul class="menu-list">
        <li v-for="(section, index) in room.sections" :key="index">
          {{ section.sectionName }}
        </li>
      </ul>
    </div>
    <div class="card-footer">
      <a @click="joinChat" href="#" class="card-footer-item">Join Chat</a>
      <a v-if="isAdmin" @click="editRoom" href="#" class="card-footer-item"
        >Edit</a
      >
      <a v-if="isAdmin" @click="deleteRoom" href="#" class="card-footer-item"
        >Delete</a
      >
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import Room from "../interfaces/Room/Room.interface";
import { useStore } from "../store";

export default defineComponent({
  name: "RoomCard",
  props: {
    room: {
      required: true,
      type: Object as () => Room,
    },
    isAdmin: {
      required: true,
      type: Boolean,
    },
  },
  setup(props, { emit }) {
    const store = useStore();
    const router = useRouter();

    const editRoom = () => {
      router.push(`/edit-room/${props.room.roomCode}`);
    };

    const deleteRoom = async () => {
      if (window.confirm("Are you sure you want do delete the room?")) {
        if (await store.dispatch("deleteRoom", props.room.roomCode)) {
          emit("reload");
        }
      }
    };

    return {
      editRoom,
      deleteRoom,
    };
  },
});
</script>

<style scoped>
.card {
  margin: 25px 0;
}
li {
  margin: 10px 10px;
}
</style>

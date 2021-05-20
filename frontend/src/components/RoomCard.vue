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
      <!--<router-link :to="chatroute" class="card-footer-item"
        >Join Chat</router-link
      >-->
      <router-link
        v-if="isAdmin"
        :to="`/edit-room/${room.roomCode}`"
        class="card-footer-item"
        >Edit</router-link
      >
      <a v-if="isAdmin" @click="deleteRoom" class="card-footer-item">Delete</a>
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

    const deleteRoom = async () => {
      if (window.confirm("Are you sure you want do delete the room?")) {
        if (await store.dispatch("deleteRoom", props.room.roomCode)) {
          emit("reload");
        }
      }
    };

    return {
      deleteRoom,
    };
  },
});
</script>

<style scoped>
li {
  margin: 10px 10px;
}
</style>

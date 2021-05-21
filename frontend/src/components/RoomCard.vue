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
      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Total hours booked</p>
            <p class="title" v-if="stats.totalHoursOfReservations !== null">
              {{ stats.totalHoursOfReservations }}
            </p>
            <p class="title" v-else>0</p>
          </div>
        </div>
      </nav>
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
import { defineComponent, onMounted, ref, Ref } from "vue";
import Room from "../interfaces/Room/Room.interface";
import RoomStats from "../interfaces/Room/RoomStats.interface";
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
    const stats: Ref<RoomStats> = ref({
      totalHoursOfReservations: 0,
    });

    onMounted(async () => {
      const response = await store.dispatch("getRoomStatistics", props.room.roomCode);
      if (response === null) return;
      stats.value = response;
    });

    const deleteRoom = async () => {
      if (window.confirm("Are you sure you want do delete the room?")) {
        if (await store.dispatch("deleteRoom", props.room.roomCode)) {
          emit("reload");
        }
      }
    };

    return {
      deleteRoom,
      stats,
    };
  },
});
</script>

<style scoped>
li {
  margin: 10px 10px;
}
</style>

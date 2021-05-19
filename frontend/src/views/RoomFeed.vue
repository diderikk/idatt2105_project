<template>
  <div>
    <input
      v-model="searchInput"
      class="input"
      type="text"
      placeholder="Search"
    />
    <div v-if="rooms.length === 0" class="box">No users available</div>
    <span v-else>
      <room-card
        v-for="(room, index) in availableRooms"
        :key="index"
        :room="room"
        :isAdmin="isAdmin"
        @reload="reload(false)"
        >{{ room }}</room-card
      >
    </span>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, Ref, ref } from "vue";
import { useStore } from "../store";
import Room from "../interfaces/Room/Room.interface";
import RoomCard from "../components/RoomCard.vue";

export default defineComponent({
  name: "RoomFeed",
  components: { RoomCard },
  setup() {
    const store = useStore();
    const searchInput = ref("");
    const rooms = ref([] as Room[]);

    onMounted(async () => {
      await reload(true);
    });

    const reload = async (editSnackBar: boolean) => {
      const response = await store.dispatch("getRooms", editSnackBar);
      if (response !== null) {
        rooms.value = response;
      }
    };

    const availableRooms = computed(
      () =>
        (rooms.value.filter((room) =>
          room.roomCode.toLowerCase().startsWith(searchInput.value.toLowerCase()) || sectionContainsSeach(room)
        ))
    );

    const sectionContainsSeach = (room: Room): boolean => {
        for(const section of room.sections) if(section.sectionName.toLowerCase().startsWith(searchInput.value.toLowerCase())) return true;
        return false;
    };

    const isAdmin = computed(() => store.getters.getUser.isAdmin);

    return {
      searchInput,
      isAdmin,
      rooms,
      reload,
      availableRooms,
    };
  },
});
</script>

<style scoped></style>

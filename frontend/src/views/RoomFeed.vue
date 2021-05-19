<template>
  <div>
    <base-feed-header
      :createRoute="'/create-room'"
      @inputChange="changeInput($event, input)"
    ></base-feed-header>
    <div v-if="rooms.length === 0" class="box">No rooms available</div>
    <div v-else class="columns">
      <div
        v-for="(room, index) in availableRooms"
        :key="index"
        class="column is-half"
      >
        <room-card
          :room="room"
          :isAdmin="isAdmin"
          @reload="reload(false)"
        ></room-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { useStore } from "../store";
import BaseFeedHeader from "../components/BaseFeedHeader.vue";
import Room from "../interfaces/Room/Room.interface";
import RoomCard from "../components/RoomCard.vue";

export default defineComponent({
  name: "RoomFeed",
  components: { RoomCard, BaseFeedHeader },
  setup() {
    const store = useStore();
    const searchInput = ref("");
    const changeInput = (input: string) => {
      searchInput.value = input;
    };
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

    const availableRooms = computed(() =>
      rooms.value.filter(
        (room) =>
          room.roomCode
            .toLowerCase()
            .startsWith(searchInput.value.toLowerCase()) ||
          sectionContainsSeach(room)
      )
    );

    const sectionContainsSeach = (room: Room): boolean => {
      for (const section of room.sections)
        if (
          section.sectionName
            .toLowerCase()
            .startsWith(searchInput.value.toLowerCase())
        )
          return true;
      return false;
    };

    const isAdmin = computed(() => store.getters.getUser.isAdmin);

    return {
      searchInput,
      changeInput,
      isAdmin,
      rooms,
      reload,
      availableRooms,
    };
  },
});
</script>

<style scoped></style>

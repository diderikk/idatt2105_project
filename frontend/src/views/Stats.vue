<template>
<div>
    <div class="columns is-centered">
        <div class="column"><back-button></back-button></div>
    </div>
    <div class="columns is-centered has-text-centered">
        <div class="title">
          Statistics:
        </div>
    </div>
    <div class="card">
    <div class="card-content">
      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Users with most reservations:</p>
            <p v-if="topUsers !== null">
                <ol class="menu-list">
                    <li v-for="(user, index) in topUsers" :key="index">
                        {{ user.firstName }} {{ user.lastName }}
                    </li>
                </ol>
            </p>
            <p v-else>...</p>
          </div>
        </div>
      </nav>
    </div>
    </div>
    <div class="card">
    <div class="card-content">
      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Most popular rooms:</p>
            <p v-if="topRooms !== null">
                <ol class="menu-list">
                    <li v-for="(room, index) in topRooms" :key="index">
                        {{ room.roomCode }}
                    </li>
                </ol>
            </p>
            <p v-else>...</p>
          </div>
        </div>
      </nav>
    </div>
    </div>
    <div class="card">
    <div class="card-content">
      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Most popular sections:</p>
            <p v-if="topSections !== null">
                <ol class="menu-list">
                    <li v-for="(section, index) in topSections" :key="index">
                        {{ section.sectionName }} of room {{ section.roomCode }}
                    </li>
                </ol>
            </p>
            <p v-else>...</p>
          </div>
        </div>
      </nav>
    </div>
    </div>
</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import BackButton from "../components/BackButton.vue";
import User from "../interfaces/User/User.interface";
import { store } from "../store";
import GETRoom from "../interfaces/Room/GETRoom.interface";
import GETSection from "../interfaces/Section/GETSection.interface";
export default defineComponent({
  name: "Stats",
  components: { BackButton },
  setup() {
    const topRooms = ref([] as GETRoom[]);
    const topSections = ref([] as GETSection[]);
    const topUsers = ref([] as User[]);

    /**
     * Retrieves stats about top sections, rooms, and users
     */
    onMounted(async () => {
        const response = await Promise.all([store.dispatch("getTopUsers"), store.dispatch("getTopRooms"), store.dispatch("getTopSections")]);
        if (response[0] !== null) {
            topUsers.value = response[0];
        }
        if (response[1] !== null) {
            topRooms.value = response[1];
        }
        if (response[2] !== null) {
            topSections.value = response[2];
        }
    });

    return {
      topRooms,
      topSections,
      topUsers,
    };
  },
});
</script>
<style scoped>
li {
  padding: 2px;
}
</style>

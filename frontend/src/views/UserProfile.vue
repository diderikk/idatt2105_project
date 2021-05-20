<template>
  <div class="card">
    <div class="card-content">
      <div class="columns is-centered">
        <div class="column"><back-button></back-button></div>
      </div>
      <div class="columns is-centered has-text-centered">
        <div
          class="title"
          v-if="user.firstName !== null || user.lastName !== null"
        >
          {{ user.firstName }} {{ user.lastName }}
        </div>
        <p class="title" v-else>*No name*</p>
      </div>

      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Email</p>
            <p class="title" v-if="user.email !== null">{{ user.email }}</p>
            <p class="title" v-else>...</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Phone number</p>
            <p
              class="title"
              v-if="
                user.phoneNationalCode !== null || user.phoneNumber !== null
              "
            >
              {{ user.phoneNationalCode }}{{ user.phoneNumber }}
            </p>
            <p class="title" v-else>...</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Expiration date</p>
            <p class="title" v-if="user.expirationDate !== null">
              {{ user.expirationDate }}
            </p>
            <p class="title" v-else>...</p>
          </div>
        </div>
      </nav>

      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Total reservations</p>
            <p class="title" v-if="userStats.totalReservations !== null">
              {{ userStats.totalReservations }}
            </p>
            <p class="title" v-else>0</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Hours booked</p>
            <p class="title" v-if="userStats.totalHoursOfReservations !== null">
              {{ userStats.totalHoursOfReservations }}
            </p>
            <p class="title" v-else>0</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Favourite room</p>
            <p class="title" v-if="userStats.favouriteRoom !== null">
              {{ userStats.favouriteRoom.roomCode }}
            </p>
            <p class="title" v-else>...</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Favourite section</p>
            <p class="title" v-if="userStats.favouriteSection !== null">
              {{ userStats.favouriteSection.sectionName }}
            </p>
            <p class="title" v-else>...</p>
          </div>
        </div>
      </nav>
    </div>
    <div class="card-footer">
      <router-link :to="`/edit-user/${user.userId}`" class="card-footer-item"
        >Edit</router-link
      >
      <a @click="deleteUser" class="card-footer-item">Delete</a>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, Ref } from "vue";
import POSTSection from "../interfaces/Section/POSTSection.interface";
import BackButton from "../components/BackButton.vue";
import { useRouter } from "vue-router";
import User from "../interfaces/User/User.interface";
import UserStats from "../interfaces/User/UserStats.interface";
import { store } from "../store";
export default defineComponent({
  name: "UserProfile",
  components: { BackButton },
  props: {
    id: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const router = useRouter();
    const user: Ref<User> = ref({
      userId: -1,
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      isAdmin: false,
      expirationDate: "",
    });
    const userStats: Ref<UserStats> = ref({
      totalHoursOfReservations: 0,
      totalReservations: 0,
      favouriteRoom: {
        roomCode: "",
        sections: [] as POSTSection[],
      },
      favouriteSection: {
        sectionName: "",
      },
    });

    onMounted(async () => {
      const response = await store.dispatch("getUser", props.id);
      if (response !== null) {
        user.value = response;
      }

      const statistics = await store.dispatch("getUserStatistics", props.id);
      if (statistics !== null) {
        userStats.value = statistics;
      }
    });

    const deleteUser = async () => {
      if (window.confirm("Are you sure you want do delete the user?")) {
        if (await store.dispatch("deleteUser", user.value.userId)) {
          router.push(`/users`);
        }
      }
    };

    return {
      user,
      userStats,
      deleteUser,
    };
  },
});
</script>
<style scoped>
.level-item {
  margin-bottom: 10px;
}
.title {
  margin-bottom: 50px;
}
</style>

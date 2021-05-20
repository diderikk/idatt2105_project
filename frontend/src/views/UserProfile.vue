<template>
  <div class="card">
    <div class="card-content">
      <back-button></back-button>
      <div id="block">
        <div class="title">Information:</div>
        <span v-if="user.isAdmin" class="tag is-dark is-medium">Admin</span>
        <p>Name: {{ user.firstName }} {{ user.lastName }}</p>
        <p>Email: {{ user.email }}</p>
        <p>Phone number: {{ user.phoneNationalCode }}{{ user.phoneNumber }}</p>
        <p v-if="user.expirationDate !== null">Expiration date: {{ user.expirationDate }}</p>
        <p v-else>Expiration date: none</p>
      </div>

      <nav class="level">
        <div class="title">Statistics:</div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Total reservations</p>
            <p class="title">{{ userStats.totalReservations }}</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Hours booked</p>
            <p class="title">{{ userStats.hoursOfReservations }}</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Favourite room</p>
            <p class="title">{{ userStats.favouriteRoom.roomCode }}</p>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <p class="heading">Favourite section</p>
            <p class="title">{{ userStats.favouriteSection.sectionName }}</p>
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
      hoursOfReservations: 0,
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
        userStats.value = statistics.data;
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
span {
  margin-bottom: 20px;
}

#block {
  margin-bottom: 20px;
}
</style>

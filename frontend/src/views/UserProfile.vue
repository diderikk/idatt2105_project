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
        <span v-if="user.expirationDate !== null"
          ><p>Expiration date: {{ user.expirationDate }}</p>
        </span>
        <span v-else
          ><p>Expiration date: none</p>
        </span>
      </div>
      <div id="block">
        <div class="title">Statistics:</div>
        <p>Total reservations: {{ userStats.totalReservations }}</p>
        <p>Hours booked: {{ userStats.hoursOfReservations }}</p>
        <span v-if="userStats.favouriteRoom !== null"
          ><p>Favourite room: {{ userStats.favouriteRoom.roomCode }}</p>
        </span>
        <span v-if="userStats.favouriteSection !== null"
          ><p>
            Favourite section: {{ userStats.favouriteSection.sectionName }}
          </p>
        </span>
      </div>
    </div>
    <footer class="card-footer">
      <a @click="edit" href="#" class="card-footer-item">Edit</a>
      <a @click="deleteUser" href="#" class="card-footer-item">Delete</a>
    </footer>
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
        userStats.value = statistics;
      }
    });

    const edit = () => {
      router.push(`/edit-user/${user.value.userId}`);
    };

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
      edit,
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

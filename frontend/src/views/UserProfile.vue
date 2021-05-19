<template>
  <div class="box">
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, Ref } from "vue";
import POSTSection from "../interfaces/Section/POSTSection.interface";
import UserForm from "../interfaces/User/UserForm.interface";
import UserStats from "../interfaces/User/UserStats.interface";
import { store } from "../store";
import { UserToUserForm } from "../utils/userUtils";
export default defineComponent({
  name: "UserProfile",
  components: {
      
  },
  props: {
    id: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const user: Ref<UserForm> = ref({
      firstName: "",
      lastName: "",
      email: "",
      phoneNationalCode: "",
      phoneNumber: "",
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
    const isDoneLoadingUser = ref(false);
    const isDoneLoadingStatistics = ref(false);

    onMounted(async () => {
      const response = await store.dispatch("getUser", props.id);
      if (response !== null) {
        user.value = UserToUserForm(response.data);
        isDoneLoadingUser.value = true;
      }

      const statistics = await store.dispatch("getUserStatistics", props.id);
      if (statistics !== null) {
        userStats.value = statistics.data;
        isDoneLoadingStatistics.value = true;
      }
    });

    return {
      user,
      userStats,
      isDoneLoadingUser,
      isDoneLoadingStatistics,
    };
  },
});
</script>
<style scoped></style>
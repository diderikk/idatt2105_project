<template>
  <div>
    <input type="text" class="input" placeholder="Search" />

    <div v-if="reservations.length === 0" class="box">No users available</div>
    <span v-else>
      <reservation-card
        v-for="(reservation, index) in reservations"
        :key="index"
        :reservation="reservation"
        @reload="reload(false)"
        >{{ reservation }}</reservation-card
      >
    </span>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from "vue";
import ReservationCard from "../components/ReservationCard.vue";
import Reservation from "../interfaces/Reservation/Reservation.interface";
import { useStore } from "../store";
import { POSTReservationToReservationForm } from "../utils/reservationUtils";
export default defineComponent({
  name: "ReservationFeed",
  components: { ReservationCard },
  setup() {
    const store = useStore();
    const reservations = ref([] as Reservation[]);

    onMounted(async () => {
      await reload(true);
    });

    const reload = async (editSnackBar: boolean) => {
      const response = await store.dispatch("getReservations", editSnackBar);
      if (response !== null) {
        reservations.value = response.map((reservation: Reservation) => {
          return {
            reservationId: reservation.reservationId,
            ...POSTReservationToReservationForm(reservation),
          };
        });
      }
    };

    return {
      reservations,
      reload,
    };
  },
});
</script>

<style scoped>
.reservation-card {
  margin: 25px 0px;
}
</style>

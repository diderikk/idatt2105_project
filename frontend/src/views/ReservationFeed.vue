<template>
  <div>
    <input type="text" class="input" placeholder="Search" />

    <div v-for="(reservation, index) in reservations" :key="index">
      <reservation-card
        :reservation="reservation"
        @delete="deleteReservation($event, id)"
        class="reservation-card"
      ></reservation-card>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onBeforeMount, ref } from "vue";
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

    const deleteReservation = (id: number) => {
      reservations.value.splice(
        reservations.value.findIndex(
          (reservation) => reservation.reservationId === id
        ),
        1
      );
    };
    onBeforeMount(async () => {
      const response = await store.dispatch("getReservations");
      if (response !== null) {
        reservations.value = response.map((reservation: Reservation) => {
          return {
            reservationId: reservation.reservationId,
            ...POSTReservationToReservationForm(reservation),
          };
        });
      }
    });

    return {
      reservations,
      deleteReservation,
    };
  },
});
</script>

<style scoped>
.reservation-card {
  margin: 25px 0px;
}
</style>

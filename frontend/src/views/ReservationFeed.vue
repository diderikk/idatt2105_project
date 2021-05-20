<template>
  <div>
    <base-feed-header
      :createRoute="'/create-reservation'"
      @inputChange="changeInput($event, input)"
    ></base-feed-header>
    <div v-if="reservations.length === 0" class="box">
      No reservations available
    </div>
    <div v-else class="columns">
      <div
        v-for="(reservation, index) in reservations"
        :key="index"
        class="column is-half"
      >
        <reservation-card
          :reservation="reservation"
          @reload="deleteReservation($event, id)"
        ></reservation-card>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, onMounted, ref } from "vue";
import ReservationCard from "../components/ReservationCard.vue";
import BaseFeedHeader from "../components/BaseFeedHeader.vue";
import Reservation from "../interfaces/Reservation/Reservation.interface";
import { useStore } from "../store";
import { POSTReservationToReservationForm } from "../utils/reservationUtils";
export default defineComponent({
  name: "ReservationFeed",
  components: { ReservationCard, BaseFeedHeader },
  setup() {
    const store = useStore();
    const searchInput = ref("");
    const changeInput = (input: string) => {
      searchInput.value = input;
    };
    const reservations = ref([] as Reservation[]);

    //TODO change to reload method as shown in the other feeds
    const deleteReservation = (id: number) => {
      reservations.value.splice(
        reservations.value.findIndex(
          (reservation) => reservation.reservationId === id
        ),
        1
      );
    };
    onMounted(async () => {
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
      changeInput,
      reservations,
      deleteReservation,
    };
  },
});
</script>

<style scoped></style>

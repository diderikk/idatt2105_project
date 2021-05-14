<template>
  <div>
    <h3>{{ reservation.sections[0].roomCode }}</h3>
    <p v-for="(section, index) in reservation.sections" :key="index">
      {{ section.name }}
    </p>
    <p>From: {{ reservation.reservationStartTime }}</p>
    <p>To: {{ reservation.reservationEndTime }}</p>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Reservation from "../interfaces/Reservation.interface";
export default defineComponent({
  name: "ReservationCard",
  props: {
    reservation: {
      required: true,
      type: Object as () => Reservation,
    },
  },
  setup(props) {},
});
</script>

<style scoped></style>

<template>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">{{ reservation.roomCode }}</h2>
    </div>
    <div class="card-content">
      <div class="content">
        <label class="label">Sections:</label>
        <ul>
          <li
            v-for="(section, index) in sectionsSortedAlphabetically"
            :key="index"
          >
            {{ section }}
          </li>
        </ul>
        <label class="label">Description:</label>
        <p>
          {{
            reservation.reservationText.length > 0
              ? reservation.reservationText
              : None
          }}
        </p>
        <label class="label">Partcipants: </label>
        <p>{{ reservation.amountOfPeople }}</p>
        <label class="label">Time:</label>
        <p>
          {{ startTimeString }} &#8594;
          {{ endTimeString }}
        </p>
      </div>
    </div>
    <div class="card-footer">
      <a class="card-footer-item" @click="viewReservation">View</a>
      <a class="card-footer-item" @click="editReservation">Edit</a>
      <a class="card-footer-item" @click="deleteReservation">Delete</a>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
import ReservationCard from "../interfaces/Reservation/ReservationCard.interface";
import { useStore } from "../store";
export default defineComponent({
  name: "ReservationCard",
  props: {
    reservation: {
      required: true,
      type: Object as () => ReservationCard,
    },
  },
  setup(props, { emit }) {
    const store = useStore();
    const router = useRouter();

    const startTimeString = computed(() => {
      return calcTimeString(
        props.reservation.startDate,
        props.reservation.startTime
      );
    });
    const endTimeString = computed(() => {
      return calcTimeString(
        props.reservation.endDate,
        props.reservation.endTime
      );
    });

    const sectionsSortedAlphabetically = ref([] as string[]);
    props.reservation.sections.forEach((section) => {
      sectionsSortedAlphabetically.value.push(section.sectionName);
    });
    sectionsSortedAlphabetically.value.sort((a, b) => {
      if (a.toLowerCase() < b.toLowerCase()) {
        return -1;
      }
      if (a.toLowerCase() > b.toLowerCase()) {
        return 1;
      }
      return 0;
    });

    const calcTimeString = (date: string, time: string) => {
      return time === "00:00" ? date : `${date} ${time}`;
    };

    const viewReservation = () => {
      router.push(`/reservation/${props.reservation.reservationId}`);
    };

    const editReservation = () => {
      router.push(`/edit-reservation/${props.reservation.reservationId}`);
    };

    const deleteReservation = (id: number) => {
      if (window.confirm("Are you sure you want to delete the reservation?")) {
        if (
          store.dispatch("deleteReservation", props.reservation.reservationId)
        ) {
          emit("reload", id);
        }
      }
    };
    return {
      startTimeString,
      endTimeString,
      sectionsSortedAlphabetically,
      viewReservation,
      editReservation,
      deleteReservation,
    };
  },
});
</script>

<style scoped></style>

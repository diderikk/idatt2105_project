<template>
  <div>
    <base-feed-header
      :createRoute="'/create-reservation'"
      :needsToBeAdmin="false"
      @inputChange="changeInput($event, input)"
    ></base-feed-header>
    <div id="select">
      <label class="label">Sorting Type:</label>
      <div class="select">
        <select v-model="sortingType" class="select">
          <option value="0" selected disabled>Select sorting type</option>
          <option value="1">None</option>
          <option value="2">Date/Time</option>
          <option value="3">Participants (Least-Most)</option>
          <option value="4">Participants (Most-Least)</option>
        </select>
      </div>
    </div>

    <div v-if="reservations.length === 0" class="box">
      No reservations available
    </div>
    <div v-else class="columns is-multiline">
      <div
        v-for="(reservation, index) in availableReservations"
        :key="index"
        class="column is-half"
      >
        <reservation-card @reload="reload(false)" :reservation="reservation">
        </reservation-card>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import ReservationCard from "../components/ReservationCard.vue";
import BaseFeedHeader from "../components/BaseFeedHeader.vue";
import { useStore } from "../store";
import { POSTReservationToReservationForm } from "../utils/reservationUtils";
import ReservationForm from "../interfaces/Reservation/ReservationForm.interface";
import GETReservation from "../interfaces/Reservation/GETReservation.interface";
import ReservationCardInterface from "../interfaces/Reservation/ReservationCard.interface";
export default defineComponent({
  name: "ReservationFeed",
  components: { ReservationCard, BaseFeedHeader },
  setup() {
    const store = useStore();
    const searchInput = ref("");
    const changeInput = (input: string) => {
      searchInput.value = input;
    };
    const reservations = ref([] as ReservationCardInterface[]);
    const sortingType = ref("0");

    onMounted(async () => {
      await reload(true);
    });

    const reload = async (editSnackBar: boolean) => {
      const response = await store.dispatch("getReservations", editSnackBar);
      if (response !== null) {
        reservations.value = response.map((reservation: GETReservation) => {
          return {
            reservationId: reservation.reservationId,
            ...POSTReservationToReservationForm(reservation),
            user: reservation.user,
          };
        });
      }
    };

    const availableReservations = computed(() => {
      let reservationsTemp = reservations.value;
      if (sortingType.value === "2") {
        reservationsTemp.sort((reservation1, reservation2) => {
          const startDate1 = new Date(
            reservation1.startDate + " " + reservation1.startTime
          );
          const startDate2 = new Date(
            reservation2.startDate + " " + reservation1.startTime
          );
          if (startDate1 >= startDate2) return 1;
          else return -1;
        });
      } else if (sortingType.value === "3") {
        reservationsTemp.sort((reservation1, reservation2) => {
          if (reservation1.amountOfPeople >= reservation2.amountOfPeople)
            return 1;
          else return -1;
        });
      } else if (sortingType.value === "4") {
        reservationsTemp.sort((reservation1, reservation2) => {
          if (reservation1.amountOfPeople <= reservation2.amountOfPeople)
            return 1;
          else return -1;
        });
      }

      return reservationsTemp.filter((reservation) => {
        return (
          reservation.roomCode
            .toLowerCase()
            .startsWith(searchInput.value.toLowerCase()) ||
          reservation.user.email
            .toLowerCase()
            .startsWith(searchInput.value.toLowerCase()) ||
          sectionContainsSearch(reservation)
        );
      });
    });

    const sectionContainsSearch = (reservation: ReservationForm): boolean => {
      for (const section of reservation.sections)
        if (
          section.sectionName
            .toLowerCase()
            .startsWith(searchInput.value.toLowerCase())
        )
          return true;
      return false;
    };

    return {
      changeInput,
      reservations,
      reload,
      availableReservations,
      sortingType,
    };
  },
});
</script>

<style scoped>
#placeholder {
  margin: 25px 0px;
}

#select {
  margin: 10px 0px;
}
</style>

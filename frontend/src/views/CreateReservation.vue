<template>
  <div>
    <base-reservation-form :config="config"></base-reservation-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref } from "vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import ReservationForm from "../interfaces/Reservation/ReservationForm.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import BaseReservationForm from "../components/BaseReservationForm.vue";
import { useStore } from "../store";
import { reservationFormToPOSTReservtion } from "../utils/reservationUtils";

export default defineComponent({
  name: "CreateReservation",
  components: {
    BaseReservationForm,
  },
  setup() {
    const store = useStore();

    const bookReservation = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: ReservationForm
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        await store.dispatch(
          "createReservation",
          reservationFormToPOSTReservtion(registerInformation)
        );
      }
    };

    /**
     * The config object to be sent to BaseReservationForm, containing title, and buttons
     */
    const config = {
      title: "Create reservation",
      buttons: [
        {
          title: "Create reservation",
          class: "button is-link is-primary",
          action: { function: bookReservation, numberOfArgs: 3 },
        },
      ],
    };

    return {
      bookReservation,
      config,
    };
  },
});
</script>

<style scoped></style>

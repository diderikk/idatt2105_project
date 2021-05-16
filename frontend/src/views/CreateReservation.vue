<template>
  <div>
    <base-reservation-form :config="config"></base-reservation-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, Ref } from "vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import CreateReservation from "../interfaces/CreateReservation.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import BaseReservationForm from "../components/BaseReservationForm.vue";

export default defineComponent({
  name: "",
  components: {
    BaseReservationForm,
  },
  setup() {
    const bookReservation = async (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: CreateReservation
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        const reservation = {
          roomCode: registerInformation.roomCode,
          sections: registerInformation.sections,
          startTime:
            registerInformation.startDate + " " + registerInformation.startTime,
          endTime:
            registerInformation.endDate + " " + registerInformation.endTime,
          description: registerInformation.description,
          limit: registerInformation.limit,
        };
        //TODO make async call instead of console logging
        console.log(reservation);
      }
    };

    const config = {
      title: "Create reservation",
      buttons: [
        {
          title: "Create user",
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

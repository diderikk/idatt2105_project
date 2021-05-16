<template>
  <div>
    <base-reservation-form-config
      :baseReservation="reservation"
      :config="config"
    ></base-reservation-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, Ref } from "vue";
import BaseReservationFormConfig from "../components/BaseReservationForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import CreateReservation from "../interfaces/CreateReservation.interface";
import CreateUser from "../interfaces/CreateUser.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
export default defineComponent({
  name: "EditUser",
  components: {
    BaseReservationFormConfig,
  },
  setup() {
    //TODO remove testdata and do async call for actual user
    const reservation: CreateReservation = reactive({
      roomCode: "A4-121",
      sections: ["Kalle", "Hei"],
      description: "Hei",
      startDate: "2021-07-07",
      endDate: "2021-07-08",
      startTime: "02:00",
      endTime: "03:00",
      limit: "12",
    });

    const editReservation = (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: CreateUser
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        //TODO Add async call and remove content
        console.log("Edited reservation: REMOVE ME");
        console.log(registerInformation);
      }
    };

    const deleteReservation = (registerInformation: CreateUser) => {
      if (window.confirm("Are you sure you want do delete the reservation?")) {
        //TODO Add async call
        console.log("Reservation deleted: REMOVE ME");
        console.log(registerInformation);
      }
    };

    const config = {
      title: "Edit reservation",
      buttons: [
        {
          title: "Confirm edit",
          class: "button is-link is-primary",
          action: { function: editReservation, numberOfArgs: 3 },
        },
        {
          title: "Delete resertvation",
          class: "button is-danger",
          action: {
            function: deleteReservation,
            numberOfArgs: 1,
          },
        },
      ],
    };

    return {
      reservation,
      config,
      editReservation,
      deleteReservation,
    };
  },
});
</script>
<style scoped></style>

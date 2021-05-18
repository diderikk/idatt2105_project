<template>
  <div>
    <!--Displaying an unfilled form if it is loading in data, and a filled in form when the data arrives-->
    <base-reservation-form-config
      v-if="isDoneLoading"
      :baseReservation="reservation"
      :config="config"
      :reservationId="parseInt(id)"
    ></base-reservation-form-config>
    <base-reservation-form-config
      v-else
      :baseReservation="reservation"
      :config="config"
    ></base-reservation-form-config>
  </div>
</template>

<script lang="ts">
import { defineComponent, onBeforeMount, reactive, ref, Ref, watch } from "vue";
import BaseReservationFormConfig from "../components/BaseReservationForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import POSTReservation from "../interfaces/Reservation/POSTReservation.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import ReservationForm from "../interfaces/Reservation/ReservationForm.interface";
import { useStore } from "../store";
import {
  POSTReservationToResrevationForm,
  reservationFormToPOSTReservtion,
} from "../utils/reservationUtils";

export default defineComponent({
  name: "EditReservation",
  components: {
    BaseReservationFormConfig,
  },
  props: {
    id: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const store = useStore();

    onBeforeMount(async () => {
      const response: POSTReservation = await store.dispatch(
        "getReservation",
        props.id
      );
      if (response !== null) {
        reservation.value = POSTReservationToResrevationForm(response);
        isDoneLoading.value = true;
      }
    });

    const reservation: Ref<ReservationForm> = ref({
      roomCode: "",
      sections: [],
      reservationText: "",
      startDate: "",
      endDate: "",
      startTime: "",
      endTime: "",
      amountOfPeople: "",
    });

    const isDoneLoading = ref(false);

    const editReservation = (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: ReservationForm,
      reservationId: number
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        console.log(reservationId);
        store.dispatch("editReservation", {
          id: reservationId,
          ...reservationFormToPOSTReservtion(registerInformation),
        });
      }
    };

    const deleteReservation = (reservationId: number) => {
      if (window.confirm("Are you sure you want to delete the reservation?")) {
        store.dispatch("deleteReservation", reservationId);
      }
    };

    const config = {
      title: "Edit reservation",
      buttons: [
        {
          title: "Confirm edit",
          class: "button is-link is-primary",
          action: { function: editReservation, numberOfArgs: 4 },
        },
        {
          title: "Delete reservation",
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
      isDoneLoading,
      config,
      editReservation,
      deleteReservation,
    };
  },
});
</script>
<style scoped></style>

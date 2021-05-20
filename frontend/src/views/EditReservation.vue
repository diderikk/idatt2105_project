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
import { defineComponent, onBeforeMount, ref, Ref } from "vue";
import BaseReservationFormConfig from "../components/BaseReservationForm.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import POSTReservation from "../interfaces/Reservation/POSTReservation.interface";
import checksBeforeAsyncCall from "../utils/checksBeforeAsyncCall";
import ReservationForm from "../interfaces/Reservation/ReservationForm.interface";
import { useStore } from "../store";
import {
  POSTReservationToReservationForm,
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
        reservation.value = POSTReservationToReservationForm(response);
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

    /**
     * If all checks are passed editReservation action in store is called
     * @param checks in the BaseReservationForm component
     * @param statuses in the BaseReservationForm component, to check if all checks have finished successfully
     * @param registerInformation the item to be sent in the POST request from the editReservation action
     * @param reservationId the id of the reservation to be edited with POST request from the editReservation action
     */
    const editReservation = (
      checks: Array<() => void>,
      statuses: Array<Ref<InputFieldFeedbackStatus>>,
      registerInformation: ReservationForm,
      reservationId: number
    ) => {
      if (checksBeforeAsyncCall(checks, statuses)) {
        store.dispatch("editReservation", {
          id: reservationId,
          ...reservationFormToPOSTReservtion(registerInformation),
        });
      }
    };

    /**
     * Deletes a reservation
     * @param reservationId the id of the reservation to be deleted with DELETE request from the editReservation action
     */
    const deleteReservation = (reservationId: number) => {
      if (window.confirm("Are you sure you want to delete the reservation?")) {
        store.dispatch("deleteReservation", reservationId);
      }
    };

    /**
     * The config object to be sent to BaseReservationForm, containing title, and buttons
     */
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

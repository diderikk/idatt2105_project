<template>
  <div>
    <base-form-field-input
      :config="{
        title: 'Choose the room name',
        errorHelperMessage: 'Choose a room',
        successHelperMessage: 'Valid!',
        feedbackStatus: roomStatus,
      }"
    >
      <select v-model="roomCode" @change="checkRoomValidity">
        <option value="" hidden disabled>Select room</option>
        <option v-for="(room, index) in rooms" :value="room.name" :key="index">
          {{ room.roomCode }}
        </option>
      </select>
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Choose the wished sections',
        errorHelperMessage: 'Choose at least one section',
        successHelperMessage: 'Valid!',
        feedbackStatus: roomStatus,
      }"
    >
      <button>Select all</button>
      <div v-for="(section, index) in availableSections" :key="index">
        <label>{{ section.id }}</label>
        <input
          @change="handleCheckBoxChange(section)"
          :value="section.selected"
          type="checkbox"
        />
      </div>
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Start date',
        errorHelperMessage: startDateErrorMessage,
        successHelperMessage: 'Valid!',
        feedbackStatus: startDateStatus,
      }"
    >
      <input
        v-model="startDate"
        :min="minDateString"
        :max="maxDateStartDateField"
        @blur="checkStartDateValidity"
        type="date"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'End date',
        errorHelperMessage: endDateErrorMessage,
        successHelperMessage: 'Valid!',
        feedbackStatus: endDateStatus,
      }"
    >
      <input
        v-model="endDate"
        :min="minDateEndDateField"
        :max="maxDateString"
        @blur="checkEndDateValidity"
        type="date"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Start time',
        errorHelperMessage: 'Fill inn a start time',
        successHelperMessage: 'Valid!',
        feedbackStatus: startTimeStatus,
      }"
    >
      <input
        v-model="startTime"
        @blur="checkStartTimeValidity"
        :disabled="disableTimePickers"
        type="time"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'End time',
        errorHelperMessage: 'Select a time that is after the start time',
        successHelperMessage: 'Valid!',
        feedbackStatus: endTimeStatus,
      }"
    >
      <input
        v-model="endTime"
        @blur="checkEndTimeValidity"
        :disabled="disableTimePickers"
        type="time"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Enter the amount of people to use the room',
        errorHelperMessage: 'The number of people has to be between 1 and 100',
        successHelperMessage: 'Valid!',
        feedbackStatus: limitStatus,
      }"
    >
      <input
        v-model="limit"
        type="number"
        min="1"
        max="100"
        @blur="checkLimitValidity"
      />
    </base-form-field-input>
    <label>Description of room use</label>
    <textarea
      v-model="description"
      placeholder="Enter description for renting the room"
      cols="30"
      rows="10"
    ></textarea>
    <button @click="bookReservation">Book reservation</button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, Ref, ref, toRefs } from "vue";
import BaseFormFieldInput from "../components/BaseFormFieldInput.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import { dateToString, removeTimeFromDate } from "../utils/date";
import SectionForCheckBox from "../interfaces/Sections/SectionForCheckBox.interface";
import Room from "../interfaces/Room.interface";
export default defineComponent({
  name: "CreateReservation",
  components: { BaseFormFieldInput },
  setup() {
    const registerInformation = reactive({
      roomCode: "",
      sections: [] as Array<number>,
      description: "",
      startDate: "",
      startTime: "",
      endDate: "",
      endTime: "",
      limit: "",
    });

    //Rooms
    const roomStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkRoomValidity = () => {
      console.log(registerInformation.roomCode);
      rooms.value.forEach((room) => console.log(room.roomCode));
      if (
        rooms.value.some(
          (room) => room.roomCode === registerInformation.roomCode
        )
      ) {
        roomStatus.value = InputFieldFeedbackStatus.SUCCESS;
      } else roomStatus.value = InputFieldFeedbackStatus.ERROR;
    };

    //TODO remove testdata
    const rooms: Ref<Array<Room>> = ref([
      {
        roomCode: "Rom 1",
        sections: [
          {
            id: 1,
          },
          {
            id: 2,
          },
        ],
      },
      {
        roomCode: "Rom 2",
        sections: [
          {
            id: 3,
          },
        ],
      },
    ]);
    //Adding selected property to rooms so that they can be used with checkboxes
    /*rooms.value
      .find((room) => room.roomCode === registerInformation.roomCode)
      ?.sections.map((section: Section) => {
        return { ...section, selected: false };
      });*/

    //Sections

    const availableSections = computed(
      () =>
        rooms.value.find(
          (room) => room.roomCode === registerInformation.roomCode
        )?.sections
    );

    const handleCheckBoxChange = (section: SectionForCheckBox) => {
      section.selected = !section.selected;
      if (section.selected) {
        registerInformation.sections.push(section.id);
      } else {
        registerInformation.sections.splice(
          registerInformation.sections.findIndex((s) => section.id === s),
          1
        );
      }
    };

    const currentDate = new Date();
    const minDate = removeTimeFromDate(currentDate);
    const longestTimeInFuture = 6;
    const maxDate = removeTimeFromDate(currentDate);
    maxDate.setMonth(minDate.getMonth() + longestTimeInFuture);
    const minDateString = ref(dateToString(minDate));
    const maxDateString = ref(dateToString(maxDate));

    const startDateAsDate = computed(
      () => new Date(registerInformation.startDate)
    );
    const endDateAsDate = computed(() => new Date(registerInformation.endDate));

    //Start date
    const startDateStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkStartDateValidity = () => {
      console.log(startDateAsDate.value >= minDate);
      startDateStatus.value =
        startDateAsDate.value <= maxDate &&
        startDateAsDate.value >= minDate &&
        (startDateAsDate.value <= endDateAsDate.value ||
          registerInformation.endDate === "") &&
        registerInformation.startDate !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };
    const maxDateStartDateField = computed(() => {
      return registerInformation.endDate.trim() === "" ||
        maxDate < new Date(registerInformation.endDate)
        ? maxDateString.value
        : registerInformation.endDate;
    });

    const startDateErrorMessage = computed(
      () =>
        `Select a date between ${minDateString.value} and ${maxDateStartDateField.value}`
    );

    //Start time
    const startTimeStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkStartTimeValidity = () => {
      startTimeStatus.value =
        startDateAndTime.value <= endDateAndTime.value
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const startDateAndTime = computed(
      () =>
        new Date(
          registerInformation.startDate +
            "T" +
            registerInformation.startTime +
            ":00"
        )
    );

    //End date
    const endDateStatus = ref(InputFieldFeedbackStatus.NONE);

    const checkEndDateValidity = () => {
      endDateStatus.value =
        endDateAsDate.value <= maxDate &&
        endDateAsDate.value >= minDate &&
        (endDateAsDate.value >= startDateAsDate.value ||
          registerInformation.startDate === "") &&
        registerInformation.endDate !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const minDateEndDateField = computed(() => {
      return registerInformation.startDate.trim() === "" ||
        minDate > new Date(registerInformation.startDate)
        ? minDateString.value
        : registerInformation.startDate;
    });

    const endDateErrorMessage = computed(
      () =>
        `Select a date between ${minDateEndDateField.value} and ${maxDateString.value}`
    );

    //End time
    const endTimeStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkEndTimeValidity = () => {
      endTimeStatus.value =
        startDateAndTime.value <= endDateAndTime.value
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const endDateAndTime = computed(
      () =>
        new Date(
          registerInformation.endDate +
            "T" +
            registerInformation.endTime +
            ":00"
        )
    );

    const disableTimePickers = computed(
      () =>
        registerInformation.startDate === "" ||
        registerInformation.endDate === ""
    );

    //Number of people
    const limitStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkLimitValidity = () => {
      //+ converts string to number or NaN
      const limitValue = +registerInformation.limit;
      if (isNaN(limitValue) || limitValue - Math.floor(limitValue) !== 0) {
        limitStatus.value = InputFieldFeedbackStatus.ERROR;
      } else {
        limitStatus.value =
          limitValue >= 1 && limitValue <= 100
            ? InputFieldFeedbackStatus.SUCCESS
            : InputFieldFeedbackStatus.ERROR;
      }
    };

    const bookReservation = () => {
      //Want to run through every check so that the user gets all errors at once, and does not discover new errors after submitting again
      checkRoomValidity();
      checkStartDateValidity();
      checkStartTimeValidity();
      checkEndDateValidity();
      checkEndTimeValidity();
      checkLimitValidity();
      const checks = [
        roomStatus,
        startDateStatus,
        startTimeStatus,
        endDateStatus,
        endTimeStatus,
        limitStatus,
      ];
      let passed = 0;
      checks.forEach((check) => {
        if (check.value === InputFieldFeedbackStatus.SUCCESS) passed++;
      });
      if (passed === checks.length) {
        //TODO make async call
      }
    };

    return {
      ...toRefs(registerInformation),
      rooms,
      roomStatus,
      checkRoomValidity,
      minDate,
      maxDate,
      maxDateStartDateField,
      minDateEndDateField,
      minDateString,
      maxDateString,
      endDateStatus,
      endTimeStatus,
      startTimeStatus,
      startDateErrorMessage,
      endDateErrorMessage,
      startDateStatus,
      checkStartDateValidity,
      checkStartTimeValidity,
      checkEndDateValidity,
      checkEndTimeValidity,
      disableTimePickers,
      limitStatus,
      checkLimitValidity,
      bookReservation,
    };
  },
});
</script>

<style scoped></style>

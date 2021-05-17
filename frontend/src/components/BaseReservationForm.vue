<template>
  <div>
    <h1 class="title">{{ config.title }}</h1>
    <base-form-field-input
      :config="{
        title: 'Start date',
        errorHelperMessage: startDateErrorMessage,
        feedbackStatus: startDateStatus,
      }"
    >
      <input
        v-model="startDate"
        :min="minDateString"
        :max="maxDateStartDateField"
        @blur="
          checkStartDateValidity();
          assignEndDate();
        "
        type="date"
        class="input"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'End date',
        errorHelperMessage: endDateErrorMessage,
        feedbackStatus: endDateStatus,
      }"
    >
      <input
        v-model="endDate"
        :min="minDateEndDateField"
        :max="maxDateEndDateField"
        @blur="checkEndDateValidity"
        type="date"
        :disabled="disableEndDateField"
        class="input"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Start time and end time',
        errorHelperMessage: 'Fill inn a start time and end time ',
        feedbackStatus: timeStatus,
      }"
    >
      <input
        v-model="startTime"
        @blur="checkTimeValidity"
        :disabled="disableTimePickers"
        type="time"
        class="input time"
        id="left-time-input"
      />
      <input
        v-model="endTime"
        @blur="checkTimeValidity"
        :disabled="disableTimePickers"
        type="time"
        class="input time"
      />
    </base-form-field-input>
    <base-form-field-input
      :config="{
        title: 'Choose the room name',
        errorHelperMessage: 'Choose a room',
        feedbackStatus: roomStatus,
      }"
    >
      <div class="select">
        <select
          v-model="roomCode"
          @change="checkRoomValidity"
          :disabled="!isDateAndTimeSelected"
        >
          <option value="" hidden disabled>Select room</option>
          <option
            v-for="(room, index) in rooms"
            :value="room.roomCode"
            :key="index"
          >
            {{ room.roomCode }}
          </option>
        </select>
      </div>
    </base-form-field-input>
    <label class="label">Choose Sections</label>
    <p v-if="availableSections.length === 0" class="help is-danger">
      No sections available for this room during the selected time.
    </p>
    <base-form-field-input
      v-else
      :config="{
        errorHelperMessage: 'Choose at least one section',
        feedbackStatus: sectionStatus,
      }"
    >
      <button
        @click="
          selectAll();
          checkSectionValidity();
        "
        :disabled="!isDateAndTimeSelected"
        class="button is-primary"
      >
        Select all
      </button>
      <button
        @click="
          removeAll();
          checkSectionValidity();
        "
        :disabled="!isDateAndTimeSelected"
        class="button"
      >
        Remove all
      </button>
      <div v-for="(section, index) in availableSections" :key="index">
        <label class="checkbox">
          <input
            @change="
              handleCheckBoxChange(section);
              checkSectionValidity();
            "
            :value="section.selected"
            :checked="section.selected"
            type="checkbox"
            :disabled="!isDateAndTimeSelected"
          />
          {{ section.sectionName }}</label
        >
      </div>
    </base-form-field-input>
    <base-form-field-input
      :config="{
        title: 'Enter the amount of people to use the room',
        errorHelperMessage: 'The number of people has to be between 1 and 100',
        feedbackStatus: amountOfPeopleStatus,
      }"
    >
      <input
        v-model="amountOfPeople"
        type="number"
        min="1"
        max="100"
        @blur="checkAmountOfPeopleValidity"
        class="input"
      />
      <p v-if="amountOfPeopleStatusIsNone" class="helper">Beetween 1 and 100</p>
    </base-form-field-input>
    <div class="field">
      <label class="label">Description of room use</label>
      <textarea
        v-model="reservationText"
        placeholder="Enter description for renting the room"
        cols="30"
        rows="10"
        class="textarea"
      ></textarea>
    </div>
    <span v-for="(button, index) in config.buttons" :key="index">
      <button
        v-if="button.action.numberOfArgs === 4"
        :class="button.class"
        @click="
          button.action.function(
            checks,
            statuses,
            registerInformation,
            reservationId
          )
        "
      >
        {{ button.title }}
      </button>
      <button
        v-if="button.action.numberOfArgs === 3"
        :class="button.class"
        @click="button.action.function(checks, statuses, registerInformation)"
      >
        {{ button.title }}
      </button>
      <button
        v-else-if="button.action.numberOfArgs === 1"
        :class="button.class"
        @click="button.action.function(reservationId)"
      >
        {{ button.title }}
      </button>
    </span>
  </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  onMounted,
  reactive,
  Ref,
  ref,
  toRefs,
  watch,
} from "vue";
import BaseFormFieldInput from "../components/BaseFormFieldInput.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import { dateToString, removeTimeFromDate } from "../utils/date";
import SectionForCheckBox from "../interfaces/Section/SectionForCheckBox.interface";
import Room from "../interfaces/Room.interface";
import BaseFormConfig from "../interfaces/config/BaseFormConfig.interface";
import Section from "../interfaces/Section/Section.interface";
import ReservationForm from "../interfaces/Reservation/ReservationForm.interface";
export default defineComponent({
  name: "BaseReservationForm",
  components: { BaseFormFieldInput },
  props: {
    config: {
      required: true,
      type: Object as () => BaseFormConfig,
    },
    baseReservation: {
      required: false,
      type: Object as () => ReservationForm,
    },
    reservationId: {
      required: false,
      type: Number,
    },
  },
  setup(props) {
    //Object containing all the information to be used in the form
    const registerInformation: ReservationForm = reactive(
      Object.assign(
        {},
        props.baseReservation ?? {
          roomCode: "",
          sections: [] as Array<string>,
          reservationText: "",
          startDate: dateToString(removeTimeFromDate(new Date())),
          startTime: "",
          endDate: "",
          endTime: "",
          amountOfPeople: "",
        }
      )
    );

    //Rooms
    const roomStatus = ref(InputFieldFeedbackStatus.NONE);

    /**
     * Sets roomStatus to succes if the selected roomCode exists in the the rooms to be selected from, else it sets an ERROR
     */
    const checkRoomValidity = () => {
      if (
        rooms.value.some(
          (room) => room.roomCode === registerInformation.roomCode
        )
      ) {
        roomStatus.value = InputFieldFeedbackStatus.SUCCESS;
      } else roomStatus.value = InputFieldFeedbackStatus.ERROR;
    };

    //TODO remove testdata and replace with async call to server
    const rooms: Ref<Array<Room>> = ref([
      {
        roomCode: "A4-112",
        sections: [
          {
            sectionName: "Bord 1",
          },
        ],
      },
      {
        roomCode: "Rom 2",
        sections: [
          {
            sectionName: "3",
          },
        ],
      },
      {
        roomCode: "A4-121",
        sections: [
          {
            sectionName: "Kalle",
          },
          {
            sectionName: "Hei",
          },
        ],
      },
      {
        roomCode: "A4-100",
        sections: [],
      },
    ]);
    //Sorts all alphabeticalle based on the room code
    rooms.value.sort((a, b) => {
      if (a.roomCode.toLowerCase() < b.roomCode.toLowerCase()) {
        return -1;
      }
      if (a.roomCode.toLowerCase() > b.roomCode.toLowerCase()) {
        return 1;
      }
      return 0;
    });

    //Sections
    const availableSections: Ref<SectionForCheckBox[]> = ref([]);
    const sectionStatus = ref(InputFieldFeedbackStatus.NONE);
    /**
     * Checks if at least on section is selected, and sets the section status
     */
    const checkSectionValidity = () => {
      sectionStatus.value =
        registerInformation.sections.length !== 0
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    /**
     * Maps all the sections from the selected room to SectionForCheckBox,where the initial selected value is false
     * Need a watcher since computed object cannot be mutated
     */
    const mapSections = () => {
      const sections: Section[] =
        rooms.value.find((r) => r.roomCode === registerInformation.roomCode)
          ?.sections ?? [];
      availableSections.value =
        sections?.map((s: Section) => {
          return { ...s, selected: false };
        }) ?? [];
    };

    /**
     * When a change in the selected roomCode occurs, run the mapSections function, and clear all the sections
     */
    watch(
      () => registerInformation.roomCode,
      () => {
        registerInformation.sections = [];
        mapSections();
      }
    );

    /**
     * runs mapSection
     * If there are passed selected sections to the config object, then make them selected
     */
    onMounted(() => {
      mapSections();

      if (registerInformation.sections.length !== 0) {
        availableSections.value.forEach((section) => {
          const index = registerInformation.sections.findIndex(
            (s) => s === section.sectionName
          );
          if (index >= 0) {
            availableSections.value[index].selected = true;
          }
        });
      }
    });

    /**
     * Selects or deselects a section
     * Adds or removes the section name to/from the registerInformation object (depending on the check value)
     * @param section The section to be selected or deselected
     */
    const handleCheckBoxChange = (section: SectionForCheckBox) => {
      section.selected = !section.selected;
      if (section.selected) {
        registerInformation.sections.push(section.sectionName);
      } else {
        registerInformation.sections.splice(
          registerInformation.sections.findIndex(
            (s) => s === section.sectionName
          ),
          1
        );
      }
    };

    /**
     * Selects all of the sections for the current roomCode
     * Adds all sections belonging to the current roomCode to the registerInformation  object
     */
    const selectAll = () => {
      availableSections.value.forEach((sectionForCheckBox) => {
        sectionForCheckBox.selected = true;
        if (
          !registerInformation.sections.some(
            (sectionName) => sectionName === sectionForCheckBox.sectionName
          )
        )
          registerInformation.sections.push(sectionForCheckBox.sectionName);
      });
    };

    /**
     * Removes all of the sections for the current roomCode
     * Removes all sections belonging to the current roomCode of the registerInformation object
     */
    const removeAll = () => {
      availableSections.value.forEach((sectionForCheckBox) => {
        sectionForCheckBox.selected = false;
      });
      registerInformation.sections = [];
    };

    const currentDate = new Date();
    //The minimum allowed date
    const minDate = removeTimeFromDate(currentDate);
    //Not allowed too book reservation more than half a year in advance
    const longestTimeInFutureAllowedInMonths = 6;
    //Not allow to reserve a room for more than a week
    const maxDaysDifferenceBetweenStartAndEndDate = 7;

    const maxDate = removeTimeFromDate(currentDate);
    maxDate.setMonth(minDate.getMonth() + longestTimeInFutureAllowedInMonths);

    const minDateString = ref(dateToString(minDate));
    const maxDateString = ref(dateToString(maxDate));

    /**
     * Returns the startDate from the datePicker as a date, to be ble to do mathematical operations with it
     */
    const startDateAsDate = computed(
      () => new Date(registerInformation.startDate)
    );

    /**
     * Returns the endDate from the datePicker as a date, to be ble to do mathematical operations with it
     */
    const endDateAsDate = computed(() => new Date(registerInformation.endDate));

    //Is NONE when an empty start time is passed, or Success when a startime is passed
    const startDateStatus = ref(
      registerInformation.startTime === ""
        ? InputFieldFeedbackStatus.NONE
        : InputFieldFeedbackStatus.SUCCESS
    );

    /**
     * Sets startDateStatus to SUCCESS if its a valid date
     */
    const checkStartDateValidity = () => {
      startDateStatus.value =
        startDateAsDate.value <= maxDate &&
        startDateAsDate.value >= minDate &&
        (startDateAsDate.value <= endDateAsDate.value ||
          registerInformation.endDate === "") &&
        registerInformation.startDate !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    /**
     * Assigns end date based on the selected start date
     * Meaning that when a users selects a date 3 months from now the user does not need to go 3 months in the future when using the end date datpicker
     */
    const assignEndDate = () => {
      if (registerInformation.startDate.trim() !== "")
        registerInformation.endDate = registerInformation.startDate;
    };

    /**
     * Returns the max date for the startDate picker
     */
    const maxDateStartDateField = computed(() => {
      return registerInformation.endDate.trim() === "" ||
        maxDate < new Date(registerInformation.endDate)
        ? maxDateString.value
        : registerInformation.endDate;
    });

    /**
     * Calculates error message for starDate based on the constraints available
     */
    const startDateErrorMessage = computed(
      () =>
        `Select a date between ${minDateString.value} and ${maxDateStartDateField.value}`
    );

    //End date
    const endDateStatus = ref(InputFieldFeedbackStatus.NONE);

    /**
     * Sets endDateStatus to SUCCESS if endDate is valid, or ERROR if it is not valid
     */
    const checkEndDateValidity = () => {
      endDateStatus.value =
        endDateAsDate.value <= maxDate &&
        endDateAsDate.value >= minDate &&
        endDateAsDate.value >= startDateAsDate.value &&
        registerInformation.endDate !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    /**
     * Disabling endDate datePicker if a startDate is not selected
     */
    const disableEndDateField = computed(() => {
      return registerInformation.startDate.trim() === "";
    });

    /**
     * Calculates the minimum date allowed for the endDate datePicker
     */
    const minDateEndDateField = computed(() => {
      return registerInformation.startDate.trim() === "" ||
        minDate > new Date(registerInformation.startDate)
        ? minDateString.value
        : registerInformation.startDate;
    });

    /**
     * Calculates the max date allowed for the endDate datePicker
     */
    const maxDateEndDateField = computed(() => {
      if (registerInformation.startDate.trim() === "") {
        return maxDateString.value;
      }
      const tempDate = new Date(startDateAsDate.value);
      tempDate.setDate(
        startDateAsDate.value.getDate() +
          maxDaysDifferenceBetweenStartAndEndDate
      );
      return maxDate > tempDate ? dateToString(tempDate) : maxDateString.value;
    });

    /**
     * Computes error message for the
     */
    const endDateErrorMessage = computed(
      () =>
        `Select a date between ${minDateEndDateField.value} and ${maxDateEndDateField.value}`
    );

    /**
     * Adds start date and start time together
     */
    const startDateAndTime = computed(
      () =>
        new Date(
          registerInformation.startDate +
            "T" +
            registerInformation.startTime +
            ":00"
        )
    );

    /**
     * Adds end date and end time together
     */
    const endDateAndTime = computed(
      () =>
        new Date(
          registerInformation.endDate +
            "T" +
            registerInformation.endTime +
            ":00"
        )
    );

    //Time
    const timeStatus = ref(InputFieldFeedbackStatus.NONE);
    /**
     * Sets timeStatus to SUCCESS if the selected times are valid, and ERROR if not
     */
    const checkTimeValidity = () => {
      timeStatus.value =
        registerInformation.startTime === "" ||
        registerInformation.endTime === "" ||
        startDateAndTime.value >= endDateAndTime.value
          ? InputFieldFeedbackStatus.ERROR
          : InputFieldFeedbackStatus.SUCCESS;
    };

    /**
     * Disables time pickers if the startDate and endDate are not selected
     */
    const disableTimePickers = computed(
      () =>
        registerInformation.startDate === "" ||
        registerInformation.endDate === ""
    );

    /**
     * Checks if both start and end date is selected, and start and end time is selected
     */
    const isDateAndTimeSelected = computed(() => {
      return (
        registerInformation.startDate.trim() !== "" &&
        registerInformation.endDate.trim() !== "" &&
        registerInformation.startTime.trim() !== "" &&
        registerInformation.endTime.trim() !== ""
      );
    });

    //Number of people
    const amountOfPeopleStatus = ref(InputFieldFeedbackStatus.NONE);
    const amountOfPeopleStatusIsNone = computed(
      () => amountOfPeopleStatus.value === InputFieldFeedbackStatus.NONE
    );

    /**
     * Sets amountOfPeopleStatus to SUCCESS if between 1 and 100 people have been added, else it is set to ERROR
     */
    const checkAmountOfPeopleValidity = () => {
      //+ converts string to number or NaN
      const amountOfPeopleValue = +registerInformation.amountOfPeople;
      if (
        isNaN(amountOfPeopleValue) ||
        amountOfPeopleValue - Math.floor(amountOfPeopleValue) !== 0
      ) {
        amountOfPeopleStatus.value = InputFieldFeedbackStatus.ERROR;
      } else {
        amountOfPeopleStatus.value =
          amountOfPeopleValue >= 1 && amountOfPeopleValue <= 100
            ? InputFieldFeedbackStatus.SUCCESS
            : InputFieldFeedbackStatus.ERROR;
      }
    };

    const checks = ref([
      checkRoomValidity,
      checkSectionValidity,
      checkStartDateValidity,
      checkEndDateValidity,
      checkTimeValidity,
      checkAmountOfPeopleValidity,
    ]);

    const statuses = ref([
      roomStatus,
      sectionStatus,
      startDateStatus,
      endDateStatus,
      timeStatus,
      amountOfPeopleStatus,
    ]);
    return {
      //Also need the basic registerInformation object to be able to call it from @click which is not possible when using ...toRefs()
      registerInformation,
      ...toRefs(registerInformation),
      rooms,
      roomStatus,
      checkRoomValidity,
      availableSections,
      selectAll,
      removeAll,
      checkSectionValidity,
      sectionStatus,
      handleCheckBoxChange,
      minDate,
      maxDate,
      maxDateStartDateField,
      minDateEndDateField,
      maxDateEndDateField,
      minDateString,
      maxDateString,
      endDateStatus,
      disableEndDateField,
      startDateErrorMessage,
      endDateErrorMessage,
      startDateStatus,
      assignEndDate,
      checkStartDateValidity,
      checkEndDateValidity,
      checkTimeValidity,
      isDateAndTimeSelected,
      timeStatus,
      disableTimePickers,
      amountOfPeopleStatus,
      amountOfPeopleStatusIsNone,
      checkAmountOfPeopleValidity,
      checks,
      statuses,
    };
  },
});
</script>

<style scoped>
.time {
  display: inline;
  width: 49%;
}

#left-time-input {
  margin-right: 2%;
}

button {
  margin-right: 5px;
}
</style>

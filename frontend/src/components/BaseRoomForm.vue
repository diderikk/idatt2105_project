<template>
  <div>
    <back-button></back-button>
    <h1 class="title">{{ config.title }}</h1>
    <base-form-field-input
      :config="{
        title: 'Room Code',
        errorHelperMessage: roomCodeInputError,
        feedbackStatus: roomCodeStatus,
      }"
      ><input
        @blur="checkRoomCodeValidity"
        v-model="roomCode"
        type="text"
        placeholder="Room Code"
        class="input"
    /></base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Sections',
        errorHelperMessage: 'Room must have atleast one section',
        feedbackStatus: sectionsStatus,
      }"
    >
      <span @blur="addSectionHandler" id="sectionInput">
        <input
          v-model="sectionInput"
          type="text"
          placeholder="Section"
          class="input"
        />
        <button class="button is-success form" @click="addSectionHandler">
          +
        </button>
      </span>
      <div class="box" v-for="(section, index) in sections" :key="index">
        {{ section.sectionName }}
        <button
          class="button is-danger form"
          @click="removeSectionHandler(index)"
        >
          -
        </button>
      </div>
    </base-form-field-input>

    <span v-for="(button, index) in config.buttons" :key="index">
      <button
        v-if="button.action.numberOfArgs === 3"
        :class="button.class"
        @click="button.action.function(checks, statuses, registerInformation)"
      >
        {{ button.title }}
      </button>
      <button
        v-else-if="button.action.numberOfArgs === 0"
        :class="button.class"
        @click="button.action.function()"
      >
        {{ button.title }}
      </button>
    </span>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from "vue";
import BaseFormFieldInput from "../components/BaseFormFieldInput.vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import BaseFormConfig from "../interfaces/config/BaseFormConfig.interface";
import Section from "../interfaces/Section/Section.interface";
import RoomForm from "../interfaces/Room/RoomForm.interface";
import BackButton from "./BackButton.vue";
export default defineComponent({
  name: "CreateRoom",
  components: { BaseFormFieldInput, BackButton },
  props: {
    config: {
      required: true,
      type: Object as () => BaseFormConfig,
    },
    baseRoom: {
      required: false,
      type: Object as () => RoomForm,
    },
  },
  setup(props) {
    const sectionInput = ref("");
    const roomCodeInputError = ref("Enter a room code");
    const registerInformation = reactive(
      //Need object assign to create a new object, to hinder mutating a prop
      Object.assign(
        {},
        //Either uses the base room sent from the props or a default base room
        props.baseRoom ?? {
          roomCode: "",
          sections: [],
        }
      )
    );

    const roomCodeStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkRoomCodeValidity = () => {
      if (registerInformation.roomCode.trim() === "") {
        roomCodeInputError.value = "Enter a room code";
        roomCodeStatus.value = InputFieldFeedbackStatus.ERROR;
      } else if (
        !/^[A-Za-z0-9-_æøåÆØÅ]{4,}$/i.test(registerInformation.roomCode.trim())
      ) {
        roomCodeInputError.value =
          "Syntax error: Use only alphabet, numbers, -, _, no spaces and length must be greater than 3";
        roomCodeStatus.value = InputFieldFeedbackStatus.ERROR;
      } else roomCodeStatus.value = InputFieldFeedbackStatus.SUCCESS;
    };

    const sectionsStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkSectionsValidity = () => {
      sectionsStatus.value =
        registerInformation.sections.length !== 0
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const addSectionHandler = () => {
      if (sectionInput.value.trim() === "") return;
      if (
        registerInformation.sections.filter(
          (section) => section.sectionName === sectionInput.value
        ).length > 0
      )
        return;

      registerInformation.sections.unshift({
        sectionName: sectionInput.value,
      } as Section);
      checkSectionsValidity();
    };

    const removeSectionHandler = (index: number) => {
      registerInformation.sections.splice(index - 1, 1);
      checkSectionsValidity();
    };

    const checks = ref([checkRoomCodeValidity, checkSectionsValidity]);

    const statuses = ref([roomCodeStatus, sectionsStatus]);

    return {
      roomCodeInputError,
      sectionInput,
      registerInformation,
      roomCodeStatus,
      checkRoomCodeValidity,
      sectionsStatus,
      checkSectionsValidity,
      addSectionHandler,
      removeSectionHandler,
      ...toRefs(registerInformation),
      statuses,
      checks,
    };
  },
});
</script>

<style scoped>
#sectionInput {
  display: flex;
  flex-direction: row;
}

ul {
  list-style-type: none;
}

.form {
  width: 4%;
}

.box {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0%;
  padding-left: 1.2%;
  height: 2.5em;
}

.margin-left {
  margin-left: 5px;
}
</style>

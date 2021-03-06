<template>
  <div>
    <back-button></back-button>
    <h1 class="title">{{ config.title }}</h1>
    <base-form-field-input
      :config="{
        title: 'First name',
        errorHelperMessage: 'Enter a first name',
        feedbackStatus: firstNameStatus,
      }"
      ><input
        @blur="checkFirstNameValidity"
        v-model="firstName"
        type="text"
        placeholder="First name"
        class="input"
    /></base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Last name',
        errorHelperMessage: 'Enter a last name',
        feedbackStatus: lastNameStatus,
      }"
      ><input
        @blur="checkLastNameValidity"
        v-model="lastName"
        type="text"
        placeholder="Last name"
        class="input"
    /></base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Email',
        errorHelperMessage: 'Enter an email (which includes a @ and a.)',
        feedbackStatus: emailStatus,
      }"
      ><input
        @blur="checkEmailValidity"
        v-model="email"
        type="email"
        placeholder="example@example.com"
        class="input"
    /></base-form-field-input>
    <base-form-field-input
      :config="{
        title: 'Phone number',
        errorHelperMessage: 'Enter a phone number e.g. +47 12345678',
        feedbackStatus: phoneNumberStatus,
      }"
    >
      <input
        v-model="phoneNumber"
        @blur="checkPhoneNumberValidity"
        type="tel"
        placeholder="Example:+47 12345678"
        id="phoneNumber"
        class="input"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Expiration date',
        errorHelperMessage:
          'Enter an upcoming date (Admins don\'t need expiration date)',
        feedbackStatus: expirationDateStatus,
      }"
      ><input
        @blur="checkExpirationDateValidity"
        v-model="expirationDate"
        :min="tommorrowAsString"
        type="date"
        placeholder="Expiration date"
        class="input"
    /></base-form-field-input>
    <div class="field">
      <label class="checkbox"
        ><input
          type="checkbox"
          :checked="isAdmin"
          @change="
            changeUserLevel();
            checkExpirationDateValidity();
          "
        />
        Is the user an admin?</label
      >
    </div>

    <span v-for="(button, index) in config.buttons" :key="index">
      <button
        v-if="button.action.numberOfArgs === 4"
        :class="button.class"
        @click="
          button.action.function(checks, statuses, registerInformation, userId)
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
        @click="button.action.function(userId)"
      >
        {{ button.title }}
      </button>
    </span>
    <button @click="clear" class="button is-danger">Clear</button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, ref, toRefs } from "vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import BaseFormFieldInput from "../components/BaseFormFieldInput.vue";
import { dateToString, removeTimeFromDate } from "../utils/date";
import BaseFormConfig from "../interfaces/config/BaseFormConfig.interface";
import BackButton from "../components/BackButton.vue";
import UserForm from "../interfaces/User/UserForm.interface";
export default defineComponent({
  name: "BaseUserForm",
  props: {
    config: {
      required: true,
      type: Object as () => BaseFormConfig,
    },
    baseUser: {
      required: false,
      type: Object as () => UserForm,
    },
    userId: {
      required: false,
      type: Number,
    },
  },
  components: {
    BaseFormFieldInput,
    BackButton,
  },
  setup(props) {
    const registerInformation = reactive(
      //Need object assign to create a new object, to hinder mutating a prop
      Object.assign(
        {},
        //Either uses the base user sent from the props or a default base user
        props.baseUser ?? {
          firstName: "",
          lastName: "",
          email: "",
          phoneNumber: "",
          expirationDate: "",
          isAdmin: false,
        }
      )
    );

    const firstNameStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkFirstNameValidity = () => {
      firstNameStatus.value =
        registerInformation.firstName.trim() !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };
    const lastNameStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkLastNameValidity = () => {
      lastNameStatus.value =
        registerInformation.lastName.trim() !== ""
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    // Defined from the RFC 5322
    const emailRegex =
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,24}))$/;
    const emailStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkEmailValidity = () => {
      emailStatus.value =
        emailRegex.test(registerInformation.email.trim()) ||
        //Root user does not need email, and emails are unique in backend therefore no one else can create a user with that email
        registerInformation.email.trim() === "root"
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const phoneNumberStatus = ref(InputFieldFeedbackStatus.NONE);
    //Taken from answer by Remigius Stalder at: https://stackoverflow.com/questions/4338267/validate-phone-number-with-javascript
    const phoneRegex = /^[+]?[\s./0-9]*[(]?[0-9]{1,4}[)]?[-\s./0-9]*$/;

    const checkPhoneNumberValidity = () => {
      phoneNumberStatus.value = phoneRegex.test(
        registerInformation.phoneNumber.trim()
      )
        ? InputFieldFeedbackStatus.SUCCESS
        : InputFieldFeedbackStatus.ERROR;
    };

    const expirationDateStatus = ref(InputFieldFeedbackStatus.NONE);

    const currentDate = removeTimeFromDate(new Date());
    const tommorrow = new Date(currentDate);
    tommorrow.setDate(currentDate.getDate() + 1);
    const tommorrowAsString = ref(dateToString(tommorrow));

    const expirationDateAsDate = computed(
      () => new Date(registerInformation.expirationDate)
    );

    const checkExpirationDateValidity = () => {
      expirationDateStatus.value =
        (registerInformation.expirationDate.trim() !== "" &&
          expirationDateAsDate.value >= tommorrow) ||
        (registerInformation.isAdmin &&
          registerInformation.expirationDate.trim() === "")
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const changeUserLevel = () => {
      registerInformation.isAdmin = !registerInformation.isAdmin;
    };

    const checks = ref([
      checkFirstNameValidity,
      checkLastNameValidity,
      checkEmailValidity,
      checkPhoneNumberValidity,
      checkExpirationDateValidity,
    ]);

    const statuses = ref([
      firstNameStatus,
      lastNameStatus,
      emailStatus,
      phoneNumberStatus,
      expirationDateStatus,
    ]);

    const clear = () => {
      registerInformation.firstName = "";
      registerInformation.lastName = "";
      registerInformation.email = "";
      registerInformation.phoneNumber = "";
      registerInformation.expirationDate = "";
      registerInformation.isAdmin = false;
      statuses.value.forEach(
        (status) => (status.value = InputFieldFeedbackStatus.NONE)
      );
    };

    return {
      //Also need the basic registerInformation object to be able to call it from @click which is not possible when using ...toRefs()
      registerInformation,
      ...toRefs(registerInformation),
      firstNameStatus,
      checkFirstNameValidity,
      lastNameStatus,
      checkLastNameValidity,
      emailStatus,
      checkEmailValidity,
      phoneNumberStatus,
      checkPhoneNumberValidity,
      expirationDateStatus,
      tommorrowAsString,
      checkExpirationDateValidity,
      changeUserLevel,
      checks,
      statuses,
      clear,
    };
  },
});
</script>

<style scoped>
button {
  margin-right: 5px;
}
</style>

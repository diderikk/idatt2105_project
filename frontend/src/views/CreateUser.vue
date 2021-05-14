<template>
  <div>
    <h1 class="title">Create user</h1>
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
        errorHelperMessage: 'Enter the users national code and phone number',
        feedbackStatus: phoneNumberStatus,
      }"
    >
      <span>+</span>
      <input
        v-model="phoneNationalCode"
        @blur="checkPhoneNumberValidity"
        type="text"
        id="phoneNationalCode"
        class="input"
      />
      <input
        v-model="phoneNumber"
        @blur="checkPhoneNumberValidity"
        type="tel"
        placeholder="Example:12345678"
        id="phoneNumber"
        class="input"
      />
    </base-form-field-input>

    <base-form-field-input
      :config="{
        title: 'Expiration date',
        errorHelperMessage: 'Enter an upcoming date',
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
    <button @click="register" class="button is-link is-primary">
      Create user
    </button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, Ref, ref, toRefs } from "vue";
import InputFieldFeedbackStatus from "../enum/InputFieldFeedbackStatus.enum";
import BaseFormFieldInput from "../components/BaseFormFieldInput.vue";
import { dateToString, removeTimeFromDate } from "../utils/date";
export default defineComponent({
  name: "CreateUser",
  components: {
    BaseFormFieldInput,
  },
  setup() {
    const registerInformation = reactive({
      firstName: "",
      lastName: "",
      email: "",
      phoneNationalCode: "",
      phoneNumber: "",
      expirationDate: "",
    });

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

    const emailStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkEmailValidity = () => {
      emailStatus.value =
        registerInformation.email.trim() !== "" &&
        registerInformation.email.includes("@") &&
        registerInformation.email.includes(".")
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const phoneNumberStatus = ref(InputFieldFeedbackStatus.NONE);
    const checkPhoneNumberValidity = () => {
      phoneNumberStatus.value =
        //Solomon islands have 5 digits, thats the smallest I could find
        registerInformation.phoneNumber.trim().length >= 5 &&
        !isNaN(+registerInformation.phoneNumber.trim()) &&
        registerInformation.phoneNationalCode.trim().length >= 1 &&
        !isNaN(+registerInformation.phoneNationalCode.trim())
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
        registerInformation.expirationDate.trim() !== "" &&
        expirationDateAsDate.value >= tommorrow
          ? InputFieldFeedbackStatus.SUCCESS
          : InputFieldFeedbackStatus.ERROR;
    };

    const register = () => {
      //Want to give the user all feddback immedeatly, and not one at a time
      checkFirstNameValidity();
      checkLastNameValidity();
      checkEmailValidity();
      checkPhoneNumberValidity();
      checkExpirationDateValidity();
      const statuses = [
        firstNameStatus,
        lastNameStatus,
        emailStatus,
        phoneNumberStatus,
        expirationDateStatus,
      ];
      let numberOfPassedStatuses = 0;
      statuses.forEach((status) => {
        if (status.value === InputFieldFeedbackStatus.SUCCESS)
          numberOfPassedStatuses++;
      });
      if (numberOfPassedStatuses === statuses.length) {
        //TODO POST to user to users
        console.log(registerInformation);
      }
    };

    return {
      ...toRefs(registerInformation),
      firstNameStatus,
      checkFirstNameValidity,
      lastNameStatus,
      checkLastNameValidity,
      emailStatus,
      checkEmailValidity,
      phoneNumberStatus,
      checkPhoneNumberValidity,
      register,
      expirationDateStatus,
      tommorrowAsString,
      checkExpirationDateValidity,
    };
  },
});
</script>

<style scoped>
#phoneNationalCode {
  width: 3vw;
  display: inline;
  margin-left: 10px;
}
#phoneNumber {
  display: inline;
  width: 92.1%;
  margin-left: 10px;
}
</style>

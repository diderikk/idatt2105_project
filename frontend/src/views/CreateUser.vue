<template>
  <div>
    <h1>Create user</h1>
    <label>First name</label>
    <input v-model="firstName" type="text" placeholder="First name" />
    <p>Enter a first name</p>
    <label>Last name</label>
    <input v-model="lastName" type="text" placeholder="Last name" />
    <p>Enter a last name</p>
    <label>Email</label>
    <input v-model="email" type="email" placeholder="example@example.com" />
    <p v-if="!validEmail && !emailIsEmpty">
      Email needs to contain a @ and a .
    </p>
    <p v-else-if="!emailIsEmpty">Valid email</p>
    <label>Phone number</label>
    <input v-model="phoneNumber" type="tel" placeholder="Example:12345678" />
    <label>Expiration date</label>
    <select v-model="expirationYear">
      <option value="0" hidden disabled>Select year</option>
      <option
        v-for="(year, index) in availableYears"
        :value="year"
        :key="index"
      >
        {{ year }}
      </option>
    </select>
    <select v-model="expirationMonth">
      <option value="0" hidden disabled>Select month</option>
      <option v-for="index in 12" :value="index" :key="index">
        {{ index }}
      </option>
    </select>
    <select v-model="expirationDayOfMonth">
      <option value="0" hidden disabled>Select date</option>
      <option v-for="index in daysInCurrentMonth" :value="index" :key="index">
        {{ index }}
      </option>
    </select>
    <button @click="register">Create user</button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, toRefs } from "vue";
export default defineComponent({
  name: "CreateUser",
  setup() {
    const registerInformation = reactive({
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      expirationYear: 0,
      expirationMonth: 0,
      expirationDayOfMonth: 0,
    });

    const expirationDate = computed(
      () =>
        registerInformation.expirationYear +
        "-" +
        (registerInformation.expirationMonth < 10 ? "0" : "") +
        registerInformation.expirationMonth +
        "-" +
        (registerInformation.expirationDayOfMonth < 10 ? "0" : "") +
        registerInformation.expirationDayOfMonth
    );

    const daysInCurrentMonth = computed(() => {
      //Need to get year here to make the computed variable also react to a change of year
      const chosenYear = registerInformation.expirationYear;
      switch (registerInformation.expirationMonth) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
          return 31;
        case 2:
          return isLeapYear(chosenYear) ? 29 : 28;
        case 4:
        case 6:
        case 9:
        case 11:
          return 30;
        default:
          return 0;
      }
    });

    const isLeapYear = (year: number) => {
      return year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
    };

    const user = computed(() => {
      return {
        firstName: registerInformation.firstName,
        lastName: registerInformation.lastName,
        email: registerInformation.email,
        phoneNumber: registerInformation.phoneNumber,
        expirationDate: expirationDate.value,
      };
    });
    const register = () => {
      console.log(user.value);
      //TODO POST to user to users
    };

    //Dont need to be inn computed because the year is static 99% of the year
    const currentYear = new Date().getFullYear();
    const availableYears = [];
    for (let i = currentYear; i < currentYear + 5; i++) {
      availableYears.push(i);
    }

    //Error checks
    const validEmail = computed(
      () =>
        registerInformation.email.includes("@") &&
        registerInformation.email.includes(".")
    );
    const emailIsEmpty = computed(
      () => registerInformation.email.trim() === ""
    );

    const isValidForm = computed(() => validEmail.value && !emailIsEmpty.value);
    return {
      ...toRefs(registerInformation),
      availableYears,
      register,
      daysInCurrentMonth,
      validEmail,
      emailIsEmpty,
    };
  },
});
</script>

<style scoped></style>

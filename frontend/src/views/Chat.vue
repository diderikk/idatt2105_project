<template>
  <div>
    <input
      type="text"
      v-model="messageInput"
      placeholder="Write a message..."
    />
    <button @click="sendMessage">Click Me</button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onBeforeUnmount, onMounted, ref } from "vue";
import { useStore } from "../store";
import Message from "../interfaces/Message.interface";
import Stomp from "stompjs";
import SockJS from "sockjs-client";
import User from "../interfaces/User/User.interface";
import { dateTimeToString } from "../utils/date";
import { webSocketURL } from "../backend";

export default defineComponent({
  name: "Chat",
  props: {
    roomCode: {
      required: true,
      type: String,
    },
  },
  setup(props) {
    const store = useStore();
    let stompClient: Stomp.Client;
    const messageInput = ref("");
    const user = computed((): User => {
      return store.getters.getUser;
    });
    const messages = ref([] as Message[]);

    onMounted(async () => {
      const response = await store.dispatch("getRoomMessages", props.roomCode);
      messages.value = response;


      const sockJs = new SockJS(`${webSocketURL}/chat/${props.roomCode}`);
      stompClient = Stomp.over(sockJs);
      stompClient.debug = () => ({});
      await connectWebSocket();
    });

    onBeforeUnmount(async () => {
      stompClient.disconnect(() => ({}));
    });

    const sendMessage = async () => {
      if(messageInput.value.trim() === "") return
      if (!stompClient.connected) await connectWebSocket();
      stompClient.send(
        `/api/v1/chat/${props.roomCode}`,
        {},
        JSON.stringify(getMessage.value)
      );
    };

    const getMessage = computed(() => {
      return {
        userId: user.value.userId,
        firstName: user.value.firstName,
        text: messageInput.value,
        timeSent: dateTimeToString(new Date()),
      } as Message;
    });

    const connectWebSocket = async () => {
      await stompClient.connect(
        {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        () => {
          stompClient.subscribe(
            `/api/v1/chat/${props.roomCode}/messages`,
            (message) => {
              console.log(message);

            }
          );
        },
        (err) => {
          //TODO: Change this
          console.log(err);
        }
      );
    };

    const sortedMessages = computed(() => {
      const tempMessages = messages.value;
      return tempMessages.sort();
    })

    return {
      sendMessage,
      messageInput,
      sortedMessages
    };
  },
});
</script>

<style scoped></style>

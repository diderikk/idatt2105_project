<template>
  <button @click="sendMessage">Click Me</button>
</template>

<script lang="ts">
import { computed, defineComponent, onBeforeUnmount, onMounted } from "vue";
import { useStore } from "../store";
import Message from "../interfaces/Message.interface";
import Stomp from "stompjs";
import SockJS from "sockjs-client";
import User from "../interfaces/User/User.interface";
import { dateToString } from "../utils/date";

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
    const user = computed(():User => {
      return store.getters.user;
    });

    onMounted(async () => {
      //TODO: Change url to backend url
      const sockJs = new SockJS("http://localhost:8080/api/v1/websocket");
      stompClient = Stomp.over(sockJs);
      stompClient.debug = () => ({});
      await connectWebSocket();
    });

    onBeforeUnmount(async () => {
      stompClient.disconnect(() => ({}));
    });

    const sendMessage = async () => {
      // if(messageInput.value === "") return
        if(!stompClient.connected) await connectWebSocket();
        stompClient.send(`/api/v1/chat/${props.roomCode}`, {}, JSON.stringify(getMessage.value));
    }

    const getMessage = computed(() => {
      return {
        userId: user.value.userId,
        firstName: user.value.firstName,
        // text: messageInput.value,
        timeSent: dateToString(new Date())
      } as Message
    });

    const connectWebSocket = async () => {
        await stompClient.connect(
        {
          Authorization: localStorage.getItem("token"),
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
    }

    return {
        sendMessage,
    }
  },
});
</script>

<style scoped></style>

<template>
  
  <div class="container">
    <back-button></back-button>
    <div id="chat" class="">
      <message-component v-for="(message, index) in sortedMessages" :key="index" :message="message"/>
    </div>
    <div class="field has-text-centered has-addons">
       <input type="text" v-model="messageInput" placeholder="Write a message..." class="input">
      <button class="button is-dark" @click="sendMessage">Send</button>
    </div>
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
import { URL } from "../backend";
import MessageComponent from "../components/Message.vue";
import BackButton from "../components/BackButton.vue";
 

export default defineComponent({
  name: "Chat",
  components: {
    MessageComponent, BackButton
  },
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

    /**
     * Gets all room messages from database
     * Connects to the rooms websocket server
     */
    onMounted(async () => {
      messages.value = await store.dispatch("getRoomMessages", props.roomCode);

      const sockJs = new SockJS(`${URL}/websocket`);
      stompClient = Stomp.over(sockJs);
      stompClient.debug = () => ({});
      await connectWebSocket();
    });

    /**
     * When unmounting, disconnect from client
     */
    onBeforeUnmount(async () => {
      stompClient.disconnect(() => ({}));
    });

    /**
     * Sends message to websocket server
     */
    const sendMessage = async () => {
      if(messageInput.value.trim() === "") return
      if (!stompClient.connected) await connectWebSocket();
      stompClient.send(
        `/api/v1/chat/${props.roomCode}`,
        {},
        JSON.stringify(getMessage.value)
      );
      messageInput.value = "";
    };

    const getMessage = computed(() => {
      return {
        userId: user.value.userId,
        firstName: user.value.firstName,
        lastName: user.value.lastName,
        text: messageInput.value,
        timeSent: dateTimeToString(new Date()),
      } as Message;
    });

    /**
     * Uses store to connect to chat
     */
    const connectWebSocket = async () => {
      await store.dispatch("connectChat", {roomCode: props.roomCode, stompClient: stompClient, messages: messages.value});
    };

    /**
     * Returns messages sorted by date/time
     */
    const sortedMessages = computed(() => {
      const tempMessages = messages.value;
      return tempMessages.sort((message1, message2) => {
          const sentDate1 = new Date(message1.timeSent);
          const sentDate2 = new Date(message2.timeSent);
          if(sentDate1 <= sentDate2) return 1;
          return -1;
      });
    })

    return {
      sendMessage,
      messageInput,
      sortedMessages,
      messages
    };
  },
});
</script>

<style scoped>
#chat{
    height: 75vh;
    display: flex;
    flex-direction: column-reverse;
    overflow-y: auto;
    overflow-x: hidden;
}
</style>

<template>
    <div class="columns">
        <div v-if="isAuthor" class="column is-one-third"></div>
        <div class="column is-two-thirds">
        <div class="messageinfo">
            <p>{{ message.firstName }} {{message.lastName}}</p>
            <p>{{ message.timeSent }}</p>
        </div>
        <div class="box">
            {{message.text}}
        </div>
        </div>
        <div v-if="!isAuthor" class="column is-one-third"></div>
    </div>
</template>


<script lang="ts">
import { computed, defineComponent } from 'vue'
import Message from '../interfaces/Message.interface'
import { useStore } from '../store'

export default defineComponent({
    name: "Message",
    props: {
        message: {
            required: true,
            type: Object as () => Message
        }
    },
    setup(props){
        const store = useStore()
        const isAuthor = computed(() => {
            return store.getters.getUser.userId === props.message.userId
        })

        return{
            isAuthor
        }
    }
})
</script>


<style scoped>
.box{
    border-radius: 20px;
    background: hsl(0, 0%, 21%);
    color: white
}

.datetext{
    text-align: end;
}

.messageinfo{
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
}
</style>

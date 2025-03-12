<template>
  <div class="modal fade show d-block" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">메시지 내용</h5>
          <button
            type="button"
            class="btn-close"
            @click="$emit('close')"
          ></button>
        </div>
        <div ref="chatBody" class="chat-body">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['chat-message', msg.type]"
          >
            <div class="bubble">{{ msg.content }}</div>
            <i
              v-if="msg.type === 'user' && msg.isRead !== 'Y'"
              class="bi bi-exclamation-circle text-danger status-icon"
            ></i>
          </div>
        </div>
        <div class="chat-footer">
          <div class="message-input mt-1 mb-2">
            <input
              v-model="newMessage"
              type="text"
              class="form-control"
              placeholder="메시지 입력..."
              @keyup.enter="handleMessageSend"
            />
            <button class="btn send-btn" @click="handleMessageSend">
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { sendMessage, getMessagesByRoomId } from '@/api/messages'
import { useStorageStore } from '@/store/index'
const props = defineProps({
  room: Object,
})
defineEmits(['close'])

const storage = useStorageStore()
const newMessage = ref('')
const messages = ref([])
const chatBody = ref(null) // 채팅창 요소

const fetchMessages = async () => {
  // 메시지 초기 조회
  try {
    const { data } = await getMessagesByRoomId(storage.getToken, props.room.id)
    messages.value = data.map(msg => ({
      ...msg,
      type: msg.senderId == storage.userId ? 'user' : 'other', // 채팅방 sender 와 receiver 구분
    }))

    await nextTick() // DOM 업데이트 후 스크롤 이동
    scrollToBottom()
  } catch (e) {
    console.error(e)
  }
}

const handleMessageSend = async () => {
  try {
    const data = ref({
      senderId: storage.getUserId,
      receiverId: props.room.otherUserId, // sender 와 receiver 는 바뀔 수 있기 때문에 현재 userId 와 비교
      content: newMessage.value,
    })
    await sendMessage(storage.getToken, data.value)
    await fetchMessages()
  } catch (e) {
    console.error(e)
  }
  newMessage.value = ''
}

const scrollToBottom = () => {
  if (chatBody.value) {
    chatBody.value.scrollTop = chatBody.value.scrollHeight
  }
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.modal {
  background: rgba(0, 0, 0, 0.5);
}

.chat-body {
  flex-grow: 1;
  padding: 10px;
  overflow-y: auto;
  max-height: 400px;
  display: flex;
  flex-direction: column;
}
.chat-message {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  max-width: 80%;
}
.chat-message.user {
  align-self: flex-end;
  justify-content: flex-end;
  flex-direction: row-reverse;
}
.chat-message.other {
  align-self: flex-start;
  justify-content: flex-start;
}
.bubble {
  padding: 12px;
  border-radius: 20px;
  word-wrap: break-word;
  max-width: 100%;
}
.chat-message.user .bubble {
  background: #007bff;
  color: white;
  border-bottom-right-radius: 5px;
}
.chat-message.other .bubble {
  background: #f1f1f1;
  color: black;
  border-bottom-left-radius: 5px;
}
.chat-footer {
  display: flex;
  padding: 10px;
  border-top: 1px solid #ccc;
  background: white;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
}
.message-input {
  display: flex;
  width: 100%;
  align-items: center;
}
.message-input input {
  flex-grow: 1;
  margin-right: 5px;
  border-radius: 20px;
  padding: 8px 12px;
  border: 1px solid #ccc;
}
.send-btn {
  background: #007bff;
  color: white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: none;
}
</style>

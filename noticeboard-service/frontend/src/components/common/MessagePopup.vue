<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="modal fade show d-block"
      tabindex="-1"
      @click.self="closePopup"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">메시지 보내기</h5>
            <button
              type="button"
              class="btn-close"
              @click="closePopup"
            ></button>
          </div>
          <div class="modal-body">
            <textarea
              class="form-control"
              v-model="message"
              placeholder="메시지를 입력하세요"
            ></textarea>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" @click="handleMessageSend">
              전송
            </button>
            <button class="btn btn-secondary" @click="closePopup">취소</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, defineEmits, defineProps } from 'vue'
import { sendMessage } from '@/api/messages'
import { useStorageStore, useToastStore } from '@/store/index'

const props = defineProps({ isOpen: Boolean, receiverId: String })
const emit = defineEmits(['update:isOpen'])
const storage = useStorageStore()
const toast = useToastStore()
const message = ref('')

const closePopup = () => {
  emit('update:isOpen', false) // 부모 컴포넌트의 isPopupOpen 필드를 false 로 변경
}

const handleMessageSend = async () => {
  if (!storage.isLogin) {
    toast.setToast('로그인해주세요.', 'danger')
    return
  }
  if (!message.value.trim()) {
    toast.setToast('메시지를 입력해주세요.', 'danger')
    return
  }

  const data = ref({
    senderId: storage.userId,
    receiverId: props.receiverId,
    content: message.value,
  })
  try {
    await sendMessage(storage.getToken, data.value)
  } catch (e) {
    console.error(e)
  }
  message.value = ''
  closePopup()
}
</script>

<style scoped>
.modal {
  background: rgba(0, 0, 0, 0.5);
}
textarea {
  width: 100%;
  height: 100px;
  margin-bottom: 10px;
}
</style>

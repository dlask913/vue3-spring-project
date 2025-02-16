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
            <button class="btn btn-primary" @click="sendMessage">전송</button>
            <button class="btn btn-secondary" @click="closePopup">취소</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, defineEmits, defineProps } from 'vue'

const props = defineProps({ isOpen: Boolean })
const emit = defineEmits(['update:isOpen', 'send'])
const message = ref('')

const closePopup = () => {
  emit('update:isOpen', false) // 부모 컴포넌트의 isPopupOpen 필드를 false 로 변경
}

const sendMessage = () => {
  if (!message.value.trim()) return
  emit('send', message.value)
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

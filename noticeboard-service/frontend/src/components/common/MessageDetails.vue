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
        <div class="modal-body">
          <p>{{ message.content }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="$emit('reply')">
            답장하기
          </button>
          <button
            type="button"
            class="btn btn-secondary"
            @click="$emit('close')"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  message: String,
})
defineEmits(['close', 'reply'])
</script>

<style scoped>
.modal {
  background: rgba(0, 0, 0, 0.5);
}
</style>

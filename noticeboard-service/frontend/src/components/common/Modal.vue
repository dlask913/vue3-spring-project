<template>
  <div
    class="modal fade"
    :id="modalId"
    tabindex="-1"
    aria-labelledby="confirmModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="confirmModalLabel">알림</h1>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <span>{{ message }}</span>
          <input
            v-if="modalId === 'inputPrice'"
            v-model="bidPrice"
            class="form-control mt-2"
          />
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            닫기
          </button>
          <button
            type="button"
            class="btn btn-primary"
            @click="isConfirmed"
            data-bs-dismiss="modal"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  message: {
    type: String,
    required: true,
  },
  modalId: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['is-confirmed'])
const bidPrice = ref(0)

const isConfirmed = async () => {
  if (props.modalId == 'confirmModal') {
    emit('is-confirmed', true)
  }
  if (props.modalId == 'inputPrice') {
    emit('input-price', true, bidPrice.value)
  }
}
</script>
<style scoped></style>

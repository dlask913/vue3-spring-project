<template>
  <div
    class="modal fade"
    id="confirmModal"
    tabindex="-1"
    aria-labelledby="confirmModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog">
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

<script>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  message: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['is-confirmed']);

const isConfirmed = async () => {
  emit('is-confirmed', true);
};
</script>
<style scoped></style>

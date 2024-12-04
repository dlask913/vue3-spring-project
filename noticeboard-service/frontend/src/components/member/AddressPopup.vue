<template>
  <div
    class="modal fade show d-block"
    tabindex="-1"
    role="dialog"
    v-if="isOpen"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">주소 선택</h5>
          <button
            type="button"
            class="btn-close"
            aria-label="Close"
            @click="closePopup"
          ></button>
        </div>
        <div class="modal-body">
          <input
            v-model="searchQuery"
            type="text"
            class="form-control mb-3"
            placeholder="주소 검색"
          />
          <button
            class="btn btn-outline-success"
            @click="searchAddress(searchQuery)"
          >
            검색하기
          </button>
          <ul class="list-group">
            <li
              v-for="address in kakaoPlaces"
              :key="address.id"
              class="list-group-item list-group-item-action"
              @click="selectAddress(address)"
            >
              <p class="mb-1 fw-bold">{{ address.road_address_name }}</p>
              <small class="text-muted">
                위도: {{ address.y }}, 경도: {{ address.x }}
              </small>
            </li>
          </ul>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="closePopup">
            닫기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { searchAddressByKakao } from '@/api/users'
defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
})
const emit = defineEmits(['close-popup', 'select-address'])

const searchQuery = ref('')
const kakaoPlaces = ref([])

const searchAddress = async query => {
  try {
    const { data } = searchAddressByKakao(query)
    kakaoPlaces.value = data
  } catch (error) {
    console.error(error)
  }
}

const closePopup = () => emit('close-popup')
const selectAddress = address => emit('select-address', address)
</script>

<style scoped>
.modal {
  background-color: rgba(0, 0, 0, 0.5);
}
</style>

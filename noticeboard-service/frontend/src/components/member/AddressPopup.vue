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
          <div class="d-flex mb-3">
            <input
              v-model="searchQuery"
              type="text"
              class="form-control mb-3 me-3"
              placeholder="주소 검색"
              @keyup.enter="searchAddress(searchQuery)"
            />
            <button
              class="btn btn-outline-success col-auto same-height"
              @click="searchAddress(searchQuery)"
            >
              검색
            </button>
          </div>
          <ul class="list-group">
            <li
              v-for="address in kakaoPlaces"
              :key="address.id"
              class="list-group-item list-group-item-action"
              @click="selectAddress(address)"
            >
              <p class="mb-1 fw-bold">{{ address.address_name }}</p>
            </li>
          </ul>
        </div>
        <!-- Pagination 추가 -->
        <Pagination
          :currentPage="params._page"
          :pageCount="pageCount"
          @page-changed="goToPage"
        />
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
import { ref, computed } from 'vue'
import { searchAddressByKakao } from '@/api/users'
import Pagination from '@/components/common/Pagination.vue'
defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
})
const emit = defineEmits(['close-popup', 'select-address'])
const params = ref({
  _page: 1,
  _limit: 10,
})
const totalCount = ref(1)
const searchQuery = ref('')
const kakaoPlaces = ref([])

const searchAddress = async query => {
  try {
    const { data } = await searchAddressByKakao(
      query,
      params.value._page,
      params.value._limit,
    )
    kakaoPlaces.value = data.documents
    totalCount.value = data.totalCount
  } catch (error) {
    console.error(error)
  }
}

const closePopup = () => emit('close-popup')
const selectAddress = address => emit('select-address', address)

const pageCount = computed(() =>
  Math.ceil(totalCount.value / params.value._limit),
)

const goToPage = page => {
  params.value._page = page
  searchAddress(searchQuery.value)
}
</script>

<style scoped>
.modal {
  background-color: rgba(0, 0, 0, 0.5);
}

.same-height {
  height: calc(1.5em + 0.75rem + 2px); /* Bootstrap의 form-control 높이 */
}
</style>

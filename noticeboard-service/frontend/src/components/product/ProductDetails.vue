<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row">
      <div class="col-12 col-md-4 text-center">
        <img :src="product.imgUrl" alt="Preview" class="product-image" />
      </div>

      <div class="col-12 col-md-6 ms-4">
        <h2>{{ product.title }}</h2>

        <label for="category" class="form-label text-muted"
          >카테고리 >> {{ product.category }}</label
        >
        <br />

        <!-- 마감일 표시 -->
        <div class="deadline-box">
          <i class="bi bi-exclamation-triangle-fill deadline-icon"></i>
          마감일: {{ product.deadline }}
        </div>
        <br />

        <div class="mb-4">
          <h3 class="card-text mt-2">{{ product.latestPrice }} 원</h3>
        </div>
        <hr />
        <div class="mb-4">
          <p class="card-text mt-2" style="min-height: 150px">
            {{ product.content }}
          </p>
        </div>

        <button
          v-if="!isDeadlineOver"
          type="submit"
          class="btn btn-primary me-2"
          @click="openModal"
          data-bs-toggle="modal"
          data-bs-target="#inputPrice"
        >
          참여하기
        </button>
        <button
          v-if="isVisible"
          class="btn btn-primary me-2"
          @click="isPopupOpen = true"
        >
          메시지 작성
        </button>
        <MessagePopup
          v-model:is-open="isPopupOpen"
          v-model:receiver-id="product.ownerId"
          @send="handleMessageSend"
        />
        <button
          type="submit"
          class="btn btn-secondary"
          @click="$router.push('/product')"
        >
          목록보기
        </button>
      </div>
    </div>
    <br />
  </div>
  <Modal
    v-show="showModal"
    :message="showMessage"
    :modalId="'inputPrice'"
    @input-price="onQuit"
  />
</template>
<script setup>
import { useRoute } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import { useStorageStore, useToastStore } from '@/store/index'
import { getProductById, createProductBid } from '@/api/products'
import Modal from '@/components/common/Modal.vue'
import MessagePopup from '@/components/common/MessagePopup.vue'

const route = useRoute()
const storage = useStorageStore()
const toast = useToastStore()

const productId = route.params.id
const showModal = ref(false)
const showMessage = ref('')
const isPopupOpen = ref(false)

const product = ref({
  id: '',
  title: '',
  content: '',
  category: '',
  standardPrice: '',
  latestPrice: '', // 가장 최신 가격
  imgUrl: '',
  postDate: '',
  ownerId: '',
  customerId: '',
})

const getProduct = async () => {
  try {
    const { data } = await getProductById(productId)
    product.value = {
      ...data,
      imgUrl: `http://localhost:8080${data.imgUrl}`,
      latestPrice: data.latestPrice ?? data.standardPrice, // latestPrice 가 null 이나 undefined 면 standardPrice 할당
    }
  } catch (error) {
    console.error(error)
  }
}

const openModal = () => {
  showModal.value = true
  showMessage.value = '가격을 입력해주세요.'
}

const onQuit = async (isConfirmed, bidPrice) => {
  if (!isConfirmed) return
  if (!storage.isLogin) {
    toast.setToast('로그인한 유저만 참여가 가능합니다.', 'danger')
    return
  }
  if (bidPrice <= product.value.standardPrice) {
    toast.setToast('현재 가격보다 더 높은 가격을 입력해주세요.', 'danger')
    return
  }

  const bidData = {
    bidPrice,
    productId: product.value.id,
    customerId: storage.getUserId,
  }
  try {
    await createProductBid(bidData)
    toast.setToast('정상적으로 가격이 입력되었습니다.')
    getProduct()
  } catch (error) {
    console.error(error)
    toast.setToast('가격 입력에 실패하였습니다.', 'danger')
  }
}

const isVisible = computed(() => {
  // 내가 작성한 글이 아닐 때만 메시지 작성 버튼 보이기
  return product.value.ownerId != storage.getUserId
})

const isDeadlineOver = computed(() => {
  // 현재 날짜가 마감일 이후면 '참여하기' 비활성화
  const today = new Date()
  const deadline = new Date(product.value.deadline)

  const todayDateOnly = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate(),
  )
  const deadlineDateOnly = new Date(
    deadline.getFullYear(),
    deadline.getMonth(),
    deadline.getDate(),
  )

  return todayDateOnly > deadlineDateOnly
})

onMounted(getProduct)
</script>
<style scoped>
.product-image {
  width: 20vw; /* 뷰포트 너비의 20% */
  height: 20vw; /* 비율 유지 */
  object-fit: cover;
  border-radius: 10%;
}
.deadline-box {
  background-color: #fff3cd; /* 연한 노랑 */
  color: #664d03; /* 어두운 갈색 텍스트 */
  border-radius: 8px;
  padding: 8px 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  margin-top: 8px;
  font-size: 0.95rem;
  width: 20vw;
}
.deadline-icon {
  color: #ff9800;
  font-size: 1.1rem;
}
</style>

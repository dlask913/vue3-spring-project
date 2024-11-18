<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row">
      <div class="col-md-4 text-center">
        <img :src="product.imgUrl" alt="Preview" class="product-image" />
      </div>
      <div class="col-md-8">
        <h2>{{ product.title }}</h2>
        <label for="category" class="form-label text-muted"
          >카테고리 >> {{ product.category }}</label
        >
        <br />

        <div class="mb-4">
          <h3 class="card-text mt-2">{{ product.standardPrice }} 원</h3>
        </div>
        <div class="mb-4">
          <p class="card-text mt-2" style="min-height: 150px">
            {{ product.content }}
          </p>
        </div>

        <button
          type="submit"
          class="btn btn-primary me-2"
          @click="openModal"
          data-bs-toggle="modal"
          data-bs-target="#inputPrice"
        >
          참여하기
        </button>
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
import { useRoute } from 'vue-router';
import { ref, onMounted } from 'vue';
import { useStorageStore, useToastStore } from '@/store/index';
import { getProductById, createProductBid } from '@/api/products';
import Modal from '@/components/common/Modal.vue';

const route = useRoute();
const storage = useStorageStore();
const toast = useToastStore();

const productId = route.params.id;
const showModal = ref(false);
const showMessage = ref('');

const product = ref({
  id: '',
  title: '',
  content: '',
  category: '',
  standardPrice: '',
  imgUrl: '',
  postDate: '',
  ownerId: '',
});

const getProduct = async () => {
  try {
    const { data } = await getProductById(productId);
    product.value = data;
    product.value.imgUrl = 'http://localhost:8080' + product.value.imgUrl;
  } catch (error) {
    console.error(error);
  }
};

const openModal = () => {
  showModal.value = true;
  showMessage.value = '가격을 입력해주세요.';
};

const onQuit = async (isConfirmed, bidPrice) => {
  if (!isConfirmed) return;

  const bidData = {
    bidPrice,
    productId: product.value.id,
    customerId: storage.getUserId,
  };
  try {
    await createProductBid(storage.getToken, bidData);
    toast.setToast('정상적으로 가격이 입력되었습니다.');
  } catch (error) {
    console.error(error);
    toast.setToast('가격 입력에 실패하였습니다.', 'danger');
  }
};

onMounted(getProduct);
</script>
<style scoped>
.product-image {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 10%;
}
</style>

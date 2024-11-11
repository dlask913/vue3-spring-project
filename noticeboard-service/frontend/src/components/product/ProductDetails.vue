<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row">
      <div class="col-md-4 text-center">
        <ImageUploader v-if="product.imgUrl" :imageUrl="product.imgUrl" />
        <h4 class="text-muted">username</h4>
      </div>
      <div class="col-md-8">
        <h3>Product Information</h3>
        <br />
        <div class="mb-3">
          <label for="category" class="form-label">카테고리</label>
          {{ product.category }}
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">최소 금액 입력</label>
          <p class="card-text mt-2">
            {{ product.standardPrice }}
          </p>
        </div>
        <div class="mb-3">
          <label for="title" class="form-label">제목</label>
          <p class="card-text mt-2">
            {{ product.title }}
          </p>
        </div>
        <div class="mb-3">
          <label for="content" class="form-label">내용</label>
          <p class="card-text mt-2">{{ product.content }}</p>
        </div>
        <button type="submit" class="btn btn-primary">참여하기</button>
      </div>
    </div>
    <br />
    <CommentList />
  </div>
</template>
<script setup>
import { useRoute } from 'vue-router';
import { ref, computed, onMounted } from 'vue';
import { useStorageStore } from '@/store/index';
import { getProductById } from '@/api/products';
import CommentList from '@/components/comment/CommentList.vue';
import ImageUploader from '../common/ImageUploader.vue';
const route = useRoute();
const storage = useStorageStore();
const productId = route.params.id;

const product = ref({
  title: '',
  content: '',
  category: '',
  standardPrice: '',
  imgUrl: '',
  postDate: '',
  ownerId: '',
});

const isEditable = computed(() => {
  return storage.getUserId == product.value.ownerId;
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

onMounted(getProduct);
</script>
<style scoped></style>

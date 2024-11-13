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

        <button type="submit" class="btn btn-primary me-2">참여하기</button>
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
</template>
<script setup>
import { useRoute } from 'vue-router';
import { ref, onMounted } from 'vue';
import { getProductById } from '@/api/products';

const route = useRoute();
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
<style scoped>
.product-image {
  width: 300px; 
  height: 300px;
  object-fit: cover;
  border-radius: 10%;
}
</style>

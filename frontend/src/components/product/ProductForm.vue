<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row">
      <div class="col-md-4 text-center">
        <ImageUploader
          v-if="productDefaultImgUrl"
          :imageUrl="productDefaultImgUrl"
        />
        <h4 class="text-muted">username</h4>
      </div>
      <div class="col-md-8">
        <h3>Product Information</h3>
        <br />
        <form @submit.prevent="onSaveProduct">
          <!-- 카테고리 ui-->
          <div class="mb-3">
            <label for="category" class="form-label">카테고리</label>
            <select class="form-select" v-model="form.category">
              <option
                v-for="category in categories"
                :key="category.id"
                :value="category"
              >
                {{ category }}
              </option>
            </select>
          </div>

          <div class="mb-3">
            <label for="email" class="form-label">최소 금액 입력</label>
            <input
              v-model="form.standardPrice"
              type="text"
              class="form-control"
              id="standardPrice"
            />
          </div>
          <div class="mb-3">
            <label for="phone" class="form-label">제목</label>
            <input
              v-model="form.title"
              type="text"
              class="form-control"
              id="title"
            />
          </div>
          <div class="mb-3">
            <label for="address" class="form-label">내용</label>
            <textarea
              v-model="form.content"
              type="text"
              class="form-control"
              id="content"
            ></textarea>
          </div>
          <button type="submit" class="btn btn-primary">Save</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import ImageUploader from '../common/ImageUploader.vue';
import { createProduct, getCategories } from '@/api/products';
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import { useStorageStore, useToastStore } from '@/store/index';

const storage = useStorageStore();
const router = useRouter();
const toast = useToastStore();

const form = ref({
  title: '',
  content: '',
  category: '',
  standardPrice: 0,
});
const productImg = ref(null);
const categories = ref([]);
const productDefaultImgUrl =
  'http://localhost:8080/image/productDefaultImg.jpg';

const onSaveProduct = async () => {
  try {
    const data = {
      ...form.value,
      ownerId: storage.getUserId,
    };
    await createProduct(storage.getToken, data, productImg);
    toast.setToast('상품 등록 완료!');
    router.push('/product');
  } catch (error) {
    console.error(error);
    toast.setToast('상품 등록 실패!', 'danger');
  }
};

const fetchCategories = async () => {
  try {
    const { data } = await getCategories();
    categories.value = data;
  } catch (error) {
    console.error(error);
  }
};

fetchCategories();
</script>

<style scoped></style>

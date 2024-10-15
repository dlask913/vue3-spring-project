<template>
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
      <form @submit.prevent="">
        <div class="mb-3">
          <label for="email" class="form-label">최소 금액 입력</label>
          <input
            v-model="product.standardPrice"
            type="text"
            class="form-control"
            id="standardPrice"
          />
        </div>
        <div class="mb-3">
          <label for="phone" class="form-label">제목</label>
          <input
            v-model="product.title"
            type="text"
            class="form-control"
            id="title"
          />
        </div>
        <div class="mb-3">
          <label for="address" class="form-label">내용</label>
          <textarea
            v-model="product.content"
            type="text"
            class="form-control"
            id="content"
          ></textarea>
        </div>
        <button type="submit" class="btn btn-primary">Save</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import ImageUploader from '../common/ImageUploader.vue';
import { ref } from 'vue';

const product = ref({});
const productDefaultImgUrl =
  'http://localhost:8080/image/productDefaultImg.jpg';
</script>

<style scoped></style>

<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row row-cols-1 row-cols-md-4 g-4 ms-2">
      <div class="col" v-for="product in products" :key="product.id">
        <div class="card">
          <img
            :src="'http://localhost:8080' + product.imgUrl"
            class="card-img-top"
            alt="Preview"
          />
          <div class="card-body">
            <h5 class="card-title">{{ product.title }}</h5>
            <p class="card-text">
              {{ product.content }}
            </p>
            <h4>{{ product.standardPrice }} 원</h4>
            <p class="card-text">
              <small class="text-muted"
                >Created at {{ product.postDate }}</small
              >
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { getProducts } from '@/api/products';
import { ref } from 'vue';

const products = ref({});

const fetchProducts = async () => {
  const { data } = await getProducts();
  products.value = data;
};

fetchProducts();
</script>
<style scoped></style>

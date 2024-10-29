<template>
  <div class="container mt-5" style="width: 70%">
    <h1 class="mb-4">상품 리스트</h1>
    <!-- 검색 기능 -->
    <SearchBar :searchOptions="searchOptions" @handle-search="searchProducts" />

    <!-- 카테고리별 상품 보기 -->
    <div class="category-container">
      <div class="category" v-for="category in categories" :key="category.name">
        <img :src="category.image" :alt="category.name" class="category-image" />
        <p>{{ category.name }}</p>
      </div>
    </div>

    <hr />
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
  <!-- Pagination 추가 -->
  <Pagination
    :currentPage="params._page"
    :pageCount="pageCount"
    @page-changed="goToPage"
  />
</template>
<script setup>
import { getProducts } from '@/api/products';
import { ref } from 'vue';
import Pagination from '@/components/common/Pagination.vue';
import SearchBar from '@/components/common/SearchBar.vue';

const products = ref({});
const searchOptions = ref([
  { key: 'title', value: '제목' },
  { key: 'username', value: '글쓴이' },
]);
const params = ref({
  _sort: 'post_date',
  _order: 'desc',
  _page: 1,
  _limit: 5,
});

const categories = [
        { name: '가구', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        { name: '침구', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        { name: '패브릭', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        { name: '조명', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        { name: '가구', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        { name: '침구', image: 'http://localhost:8080/image/productDefaultImg.jpg' },
        // 추가 카테고리들
      ];

const fetchProducts = async () => {
  const { data } = await getProducts();
  products.value = data;
};

const searchProducts = async () => {};


fetchProducts();
</script>
<style scoped>
.card-img-top {
  object-fit: cover; /* 비율을 유지하면서 크기에 맞춰 자르기 */
}

.category-container {
  display: flex;
  justify-content: center; /* 수평 중앙 정렬 */
  align-items: center; /* 수직 중앙 정렬 */
  gap: 20px; /* 요소 사이 간격 */
  padding: 20px;
}

.category {
  flex: 0 0 auto; /* 요소가 줄어들지 않도록 고정 */
  text-align: center;
}

.category-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}

.category p {
  font-size: 14px;
  color: #666;
}
</style>

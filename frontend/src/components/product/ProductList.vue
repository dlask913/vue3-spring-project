<template>
  <div class="row row-cols-1 row-cols-md-4 g-4 ms-2">
    <div class="col" v-for="product in products" :key="product.id">
      <div class="card">
        <img
          src="https://search.pstatic.net/common?type=f&size=206x206&quality=95&direct=true&src=http%3A%2F%2Fshop1.phinf.naver.net%2F20200714_207%2F1594660012036OKike_JPEG%2F30795629492135175_-293141698.jpeg"
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
            <small class="text-muted">Created at {{ product.createdAt }}</small>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const products = [
  {
    id: 1,
    title: '강아지',
    content: '강아지 물건 팔아요',
    standardPrice: 12000,
    createdAt: '2011.1.1',
  },
  {
    id: 2,
    title: '고양이',
    content: '고양이 물건 팔아요',
    standardPrice: 10000,
    createdAt: '2011.1.1',
  },
  {
    id: 3,
    title: '도마뱀',
    content: '도마뱀 물건 팔아요',
    standardPrice: 5000,
    createdAt: '2011.1.1',
  },
];
</script>
<style scoped></style>

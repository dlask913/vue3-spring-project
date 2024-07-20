<template>
  <nav class="mt-5" aria-label="Page navigation example">
    <ul class="pagination justify-content-center">
      <li class="page-item">
        <a
          class="page-link"
          href="#"
          aria-label="Previous"
          @click.prevent="goToPage(currentPage - 1)"
        >
          <span aria-hidden="true">&laquo;</span>
        </a>
      </li>
      <li
        v-for="page in pageCount"
        :key="page"
        class="page-item"
        :class="{ active: currentPage === page }"
      >
        <a class="page-link" href="#" @click.prevent="goToPage(page)">{{
          page
        }}</a>
      </li>
      <li class="page-item">
        <a
          class="page-link"
          href="#"
          aria-label="Next"
          @click.prevent="goToPage(currentPage + 1)"
        >
          <span aria-hidden="true">&raquo;</span>
        </a>
      </li>
    </ul>
  </nav>
</template>
<script setup>
import { defineEmits } from 'vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  pageCount: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['page-changed']);

const goToPage = (page) => {
  if (page < 1 || page > props.pageCount) return; // 페이지 범위를 벗어나는 경우
  emit('page-changed', page);
};
</script>
<style></style>

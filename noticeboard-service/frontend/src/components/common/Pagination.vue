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
        v-for="page in visiblePages"
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
import { computed } from 'vue'
const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  pageCount: {
    type: Number,
    required: true,
  },
})

const emit = defineEmits(['page-changed'])

const visiblePages = computed(() => {
  const maxVisiblePageSize = 5
  const pages = []
  const half = Math.floor(maxVisiblePageSize / 2)
  let start = Math.max(1, props.currentPage - half)
  let end = Math.min(props.pageCount, props.currentPage + half)

  // 보이는 페이지 버튼 수가 maxVisibleButtons보다 적은 경우
  if (end - start + 1 < maxVisiblePageSize) {
    if (start === 1) {
      end = Math.min(props.pageCount, start + maxVisiblePageSize - 1)
    } else if (end === props.pageCount) {
      start = Math.max(1, end - maxVisiblePageSize + 1)
    }
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const goToPage = page => {
  if (page < 1 || page > props.pageCount) return // 페이지 범위를 벗어나는 경우
  emit('page-changed', page)
}
</script>
<style></style>

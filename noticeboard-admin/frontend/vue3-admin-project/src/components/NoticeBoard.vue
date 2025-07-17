<template>
  <form @submit.prevent="onSaveNotice">
    <div class="container mt-5" >
      <h2 class="mt-5 mb-4 text-center"><b>게시글 작성</b></h2>
      <div class="mb-4">
        <label for="title" class="form-label">제목</label>
        <input
          type="text"
          v-model="form.title"
          class="form-control"
          placeholder="제목을 입력하세요"
          required
        />
      </div>
      <div class="mb-4">
        <label for="content" class="form-label">내용</label>
        <textarea
          v-model="form.content"
          class="form-control"
          rows="10"
          placeholder="내용을 입력하세요"
          required
        ></textarea>
      </div>
      <div class="d-flex justify-content-end">
        <button type="submit" class="btn btn-primary me-3" >
          저장
        </button>
      </div>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/axios'

const form = ref({
  title: '',
  content: '',
})

const onSaveNotice = async () => {
  try {
    const data = {
      ...form.value
    }
    await axios.post('/notice', data)
  } catch (error) {
    console.error(error)
  }
}
</script>



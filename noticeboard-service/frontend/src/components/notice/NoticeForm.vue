<template>
  <form @submit.prevent="onSaveNotice">
    <div class="container mt-5" style="width: 40%">
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
        <button type="submit" class="btn btn-primary me-3" style="width: 10%">
          {{ editing ? '수정' : '저장' }}
        </button>
        <button
          type="button"
          class="btn btn-secondary"
          style="width: 10%"
          @click="$router.push('/post')"
        >
          취소
        </button>
      </div>
    </div>
  </form>
</template>

<script setup>
import { createNotice, updateNotice, getNoticeById } from '@/api/notices'
import { useRoute, useRouter } from 'vue-router'
import { ref } from 'vue'
import { useStorageStore, useToastStore } from '@/store/index'

const props = defineProps({
  editing: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()
const router = useRouter()
const storage = useStorageStore()
const toast = useToastStore()
const form = ref({
  title: '',
  content: '',
})
const noticeId = route.params.id

const onSaveNotice = async () => {
  try {
    const data = {
      ...form.value,
      memberId: storage.getUserId,
    }
    if (props.editing) {
      await updateNotice(storage.getToken, noticeId, data)
      toast.setToast('게시글 수정 완료!')
    } else {
      await createNotice(storage.getToken, data)
      toast.setToast('게시글 저장 완료!')
    }
    router.push('/post')
  } catch (error) {
    console.error(error)
    toast.setToast('게시글 저장 실패!', 'danger')
  }
}

const getTodo = async () => {
  try {
    const { data } = await getNoticeById(storage.getToken, noticeId)
    form.value.title = data.title
    form.value.content = data.content
  } catch (error) {
    toast.setToast('게시글 데이터를 가져오지 못했습니다.', 'danger')
  }
}

if (props.editing) {
  getTodo()
}
</script>
<style></style>

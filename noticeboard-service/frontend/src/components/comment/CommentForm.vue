<template>
  <div class="card">
    <div class="card-body">
      <div class="form-group">
        <label class="form-label"><b>Comment</b></label>
        <textarea
          v-model="content"
          class="form-control"
          rows="3"
          placeholder="내용을 입력하세요"
          required
        ></textarea>
        <div v-if="valueError" class="text-red">
          {{ valueError }}
        </div>
      </div>
      <div class="d-flex justify-content-end mt-2">
        <button type="submit" class="btn btn-dark" @click="onSave">저장</button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useStorageStore, useToastStore } from '@/store/index'

const props = defineProps({
  parentId: {
    type: String,
  },
})

const emit = defineEmits(['comment-saved'])
const storage = useStorageStore()
const toast = useToastStore()

const content = ref('')
const valueError = ref('')

const onSave = async () => {
  if (!content.value) {
    valueError.value = '내용을 입력해주세요'
    return
  }
  if (!storage.isLogin) {
    toast.setToast('로그인을 한 사용자만 댓글을 작성할 수 있습니다.', 'danger')
    return
  }
  valueError.value = ''
  const data = {
    content: content.value,
    parentId: props.parentId,
  }
  emit('comment-saved', data)
  content.value = ''
}
</script>

<style scoped>
.text-red {
  color: red;
  font-size: 14px;
  margin-top: 2px;
}
</style>

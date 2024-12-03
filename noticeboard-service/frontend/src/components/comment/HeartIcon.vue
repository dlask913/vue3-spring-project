<template>
  <div :id="heartId">
    <i
      :class="isLike ? 'bi-suit-heart-fill' : 'bi-suit-heart'"
      @click="toggleHeart"
    ></i>
    <span class="fw-lighter ms-1">{{ heartCnt }}</span>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHeartStatus, saveHeart, removeHeart } from '@/api/hearts'
import { useStorageStore, useToastStore } from '@/store/index'

const props = defineProps({
  commentId: {
    type: Number,
    required: true,
  },
})

const storage = useStorageStore()
const toast = useToastStore()

const isLike = ref(false)
const heartId = ref(0)
const heartCnt = ref(0)

const toggleHeart = async () => {
  if (!storage.isLogin) {
    toast.setToast('로그인한 사용자만 좋아요를 누를 수 있습니다.', 'danger')
    return
  }
  try {
    if (!isLike.value) {
      const request = {
        memberId: storage.getUserId,
        commentId: props.commentId,
      }
      await saveHeart(storage.getToken, request)
    } else {
      await removeHeart(storage.getToken, heartId.value)
      isLike.value = false
    }
    await fetchHeartStatus()
  } catch (error) {
    console.error(error)
  }
}

const fetchHeartStatus = async () => {
  try {
    const { data } = await getHeartStatus(
      storage.getToken,
      storage.getUserId,
      props.commentId,
    )
    if (data) {
      if (storage.getUserId == data.memberId) {
        isLike.value = true
      }
      heartId.value = data.id
      heartCnt.value = data.cnt
    } else {
      isLike.value = false
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchHeartStatus)
</script>

<style scoped></style>

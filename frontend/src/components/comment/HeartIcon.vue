<template>
    <i :class="isLike ? 'bi-suit-heart-fill' : 'bi-suit-heart'" 
      @click="toggleHeart"></i>
</template>

<script>
import { ref } from 'vue';
export default {
  props: {
    noticeId: {
      type: String,
      required: true
    },
    commentId: {
      type: String,
      required: true
    },
  },
  setup(props) {
    const isLike = ref(false);
    const toggleHeart = () => {
      isLike.value = !isLike.value;
    };

    return {
      isLike,
      toggleHeart,
    }
  }
}
</script>

<style scoped>
</style>

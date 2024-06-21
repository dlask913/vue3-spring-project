<template>
  <div :id="heartId">
    <i :class="isLike ? 'bi-suit-heart-fill' : 'bi-suit-heart'" 
      @click="toggleHeart"></i>
  </div>
</template>

<script>
import { ref } from 'vue';
import { getHeartStatus, saveHeart, removeHeart } from '@/api/hearts';
import { useStorageStore } from '@/store/index';
export default {
  props: {
    commentId: {
      type: Number,
      required: true
    }
  },
  setup(props) {
    const storage = useStorageStore();
    const isLike = ref(false);
    const heartId = ref(0);

    const toggleHeart = async () => {
      try{
        if(!isLike.value){
          const request = {
            memberId: storage.getUserId,
            commentId: props.commentId,
          };
          await saveHeart(storage.getToken, request);
          await fetchHeartStatus();
        } else {
          await removeHeart(storage.getToken, heartId.value);
          isLike.value = false;
        }
      } catch(error) {
        console.error(error);
      }
    };

    const fetchHeartStatus = async () => {
      try{
        const { data } = await getHeartStatus(storage.getToken, storage.getUserId, props.commentId);
        if (data){
          isLike.value = true;
          heartId.value = data.id;
        } else{
          isLike.value = false;
        }
      }catch(error){
        console.error(error);
      }
    };

    fetchHeartStatus();

    return {
      isLike,
      toggleHeart,
      heartId,
    }
  }
}
</script>

<style scoped>
</style>

<template>
  <div :id="heartId">
    <i :class="isLike ? 'bi-suit-heart-fill' : 'bi-suit-heart'" 
      @click="toggleHeart"></i>
    <span class="fw-lighter ms-1">{{ heartCnt }}</span>
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
    const heartCnt = ref(0);

    const toggleHeart = async () => {
      try{
        if(!isLike.value){
          const request = {
            memberId: storage.getUserId,
            commentId: props.commentId,
          };
          await saveHeart(storage.getToken, request);
        } else {
          await removeHeart(storage.getToken, heartId.value);
          isLike.value = false;
        }
        await fetchHeartStatus();
      } catch(error) {
        console.error(error);
      }
    };

    const fetchHeartStatus = async () => {
      try{
        const { data } = await getHeartStatus(storage.getToken, storage.getUserId, props.commentId);
        if (data){
          if (storage.getUserId == data.memberId){
            isLike.value = true;
          }
          heartId.value = data.id;
          heartCnt.value = data.cnt;
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
      heartCnt,
    }
  }
}
</script>

<style scoped>
</style>

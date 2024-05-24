<template>
  <hr>
  <div v-for="(comment, index) in comments" :key="comment.id">
    <li class="list-group-item d-flex justify-content-between align-items-start">
      <div class="ms-2 me-auto">
        <div class="fw-bold">{{ comment.username }}</div>
        <span>{{ comment.content }}</span>
        <p class="fw-lighter">{{ comment.postDate }}</p>
      </div>
      <div v-if="isMine(comment.memberId)" class="d-flex justify-content-end mt-2">
          <a href="#" class="stretched-link me-2">수정</a>
          <a href="#" class="stretched-link me-2">삭제</a>
      </div>
    </li>
    <hr />
    
  </div>
  <CommentForm :noticeId="noticeId" @comment-saved="getComments"/>
</template>

<script>
import { ref } from 'vue';
import axios from '@/axios';
import { useRoute } from 'vue-router';
import { useStorageStore } from '@/store/index';
import CommentForm from '@/components/comment/CommentForm.vue';
export default {
  components:{
    CommentForm,
  },
  setup() {
    const storage = useStorageStore();
    const route = useRoute();
    const comments = ref([]);
    const noticeId = route.params.id;
    const getComments  = async ()  => {
      try {
        let res = await axios.get(`comment/${noticeId}`);
        comments.value = res.data;
      } catch (err){
        console.log(err);
      }
    };

    const isMine = (memberId) => {
      return storage.getUserId == memberId;
    };

    getComments();

    return {
      comments,
      isMine,
      noticeId,
      getComments,
    }
  }
}
</script>
<style>
  
</style>
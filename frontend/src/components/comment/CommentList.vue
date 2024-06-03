<template>
  <hr>
  <div v-for="(comment, index) in comments" :key="comment.id">
    <li class="list-group-item d-flex justify-content-between align-items-start">
      <div class="container" style="width: 80%;">
        <div class="fw-bold">{{ comment.username }}</div>
        <textarea v-if="isEdit(comment.id)" v-model="comment.content" class="form-control" rows="3" ></textarea>
        <span v-else >{{ comment.content }}</span>
        <p class="fw-lighter">{{ comment.postDate }}</p>
      </div>
      
      <div class="d-flex justify-content-end mt-2">
          <a v-if="isMine(comment.memberId)" href="#" class="me-2" @click.prevent="onEdit(comment)">수정</a>
          <a v-if="isMine(comment.memberId)" href="#" class="me-2" @click.prevent="onDeleteComment(comment.id)">삭제</a>
          <HeartIcon :noticeId="noticeId" :commentId="comment.id"/>
      </div>
    </li>
    <hr />
    
  </div>
  <CommentForm :noticeId="noticeId" @comment-saved="getComments"/>
</template>

<script>
import { ref } from 'vue';
import { deleteComment, getCommentsByNoticeId, updateComment } from '@/api/comments';
import { useRoute } from 'vue-router';
import { useStorageStore, useToastStore } from '@/store/index';
import CommentForm from '@/components/comment/CommentForm.vue';
import HeartIcon from '@/components/comment/HeartIcon.vue';
export default {
  components:{
    CommentForm,
    HeartIcon,
  },
  setup() {
    const storage = useStorageStore();
    const toast = useToastStore();
    const route = useRoute();
    const comments = ref([]);
    const editFlag = ref(0);
    const noticeId = route.params.id;
    const getComments  = async ()  => {
      try {
        const { data } = await getCommentsByNoticeId(noticeId);
        comments.value = data;
      } catch (error){
        console.error(error);
      }
    };

    const isMine = (memberId) => {
      return storage.getUserId == memberId;
    };

    const onDeleteComment = async (commentId) => {
      try  {
        await deleteComment(storage.getToken, commentId);  
        getComments();
        toast.setToast("댓글 삭제 완료!");
      }catch (error) {
        console.error(error);
      }
    }

    const isEdit = (commentId) => {
      return editFlag.value == commentId;
    }

    const onEdit = async (comment) => {
      if (editFlag.value === 0){
        editFlag.value = comment.id;  
        return;
      }

      try{
        const form = { 
          id: comment.id,
          content: comment.content
         };
        await updateComment(storage.getToken, comment.id , form);
        editFlag.value = 0;
        getComments();
      } catch(error) {
        console.error(error);
      }
    }

    getComments();

    return {
      comments,
      isMine,
      noticeId,
      getComments,
      onDeleteComment,
      isEdit,
      onEdit,
    }
  }
}
</script>
<style scoped>
  .ms-2.me-auto {
    max-width: 100%; /* 최대 너비를 100%로 설정하여 가능한 한 많은 공간을 차지하도록 함 */
}
</style>
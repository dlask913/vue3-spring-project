<template>
  <div>
    <ul class="list-group mt-1">
      <hr />
      <li
        v-for="reply in replies"
        :key="reply.id"
        class="list-group-item justify-content-between replies"
      >
        <div>
          <div class="comment-header">
            <img :src="hostUrl + reply.memberImgUrl" alt="Profile Picture" class="profile-picture" />
            <div class="fw-bold mt-1 mb-2">{{ reply.username }}</div>
          </div>
          <textarea
            v-if="isEdit(reply.id)"
            v-model="reply.content"
            class="form-control"
            rows="2"
          ></textarea>
          <span v-else>{{ reply.content }}</span>
          <p class="fw-lighter mt-2">{{ reply.postDate }}</p>
        </div>
        <hr />
        <div class="d-flex justify-content-end mt-2">
          <a
            v-if="isMine(reply.memberId)"
            href="#"
            class="me-2"
            @click.prevent="onEdit(reply)"
            >수정</a
          >
          <a
            v-if="isMine(reply.memberId)"
            href="#"
            class="me-2"
            @click.prevent="onDelete(reply.id)"
            >삭제</a
          >
        </div>
      </li>
    </ul>
    <div class="comment-form-container">
      <CommentForm :parentId="commentId" @comment-saved="saveReply" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {
  createReply,
  updateReply,
  deleteReply,
  getRepliesByComment,
} from '@/api/replies';
import CommentForm from '@/components/comment/CommentForm.vue';
import { useStorageStore } from '@/store';

const props = defineProps({
  commentId: {
    type: String,
  },
});
const emit = defineEmits(['update-reply-count']);

const storage = useStorageStore();
const replies = ref([]);
const isEdited = ref(0);
const hostUrl = 'http://localhost:8080';

const fetchReplis = async () => {
  try {
    const { data } = await getRepliesByComment(props.commentId);
    replies.value = data;
    emit('update-reply-count', replies.value.length); 
    console.log(replies.value);
  } catch (e) {
    console.error(e);
  }
};

const saveReply = async (data) => {
  try {
    const form = {
      content: data.content,
      commentId: data.parentId,
      memberId: storage.getUserId,
    };
    await createReply(storage.getToken, form);
    fetchReplis();
  } catch (error) {
    console.error(error);
  }
};

const onEdit = async (data) => {
  if (isEdited.value === 0) {
    isEdited.value = data.id;
    return;
  }

  try {
    await updateReply(storage.getToken, data.id, data);
    isEdited.value = 0;
  } catch (e) {
    console.error(e);
  }
};

const onDelete = async (replyId) => {
  try {
    await deleteReply(storage.getToken, replyId);
    fetchReplis();
  } catch (e) {
    console.error(e);
  }
};

const isMine = (memberId) => {
  return storage.getUserId == memberId;
};

const isEdit = (replyId) => {
  return isEdited.value == replyId;
};

onMounted(() => {
  fetchReplis();
});
</script>

<style scoped>
.replies {
  display: flex;
  border: none;
  border-bottom: 1px solid #ddd;
  margin: 0 auto;
  width: 80%;
}

.replies > span {
  margin-right: 10px;
}

.replies:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
}

.profile-picture {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

/* CommentForm의 너비를 ul과 동일하게 설정 */
.comment-form-container {
  width: 80%;
  margin: 0 auto;
}
</style>

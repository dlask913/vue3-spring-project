<template>
  <div class="card">
    <div class="card-body">
      <div class="form-group">
        <label class="form-label"><b>Comment</b></label>
        <textarea
          v-model="comment.content"
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
<script>
import { createComment } from '@/api/comments';
import { ref } from 'vue';
import { useStorageStore } from '@/store/index';
export default {
  props: {
    noticeId: {
      type: String,
      required: true,
    },
  },
  setup(props, { emit }) {
    const storage = useStorageStore();
    const comment = ref({
      content: '',
    });
    const valueError = ref('');

    const onSave = async () => {
      if (!comment.value.content) {
        valueError.value = '내용을 입력해주세요';
        return;
      }
      if (!storage.isLogin) {
        alert('로그인을 한 사용자만 댓글을 작성할 수 있습니다.');
        return;
      }
      valueError.value = '';
      try {
        const data = {
          content: comment.value.content,
          noticeId: props.noticeId,
          memberId: storage.getUserId,
        };
        await createComment(storage.getToken, data);
        comment.value.content = '';
        emit('comment-saved');
      } catch (error) {
        console.error(error);
      }
    };

    return {
      onSave,
      comment,
      valueError,
    };
  },
};
</script>
<style scoped>
.text-red {
  color: red;
  font-size: 14px;
  margin-top: 2px;
}
</style>

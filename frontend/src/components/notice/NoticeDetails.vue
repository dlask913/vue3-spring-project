<template>
  <div class="container mt-5" style="width: 60%">
    <h2>{{ notice.title }}</h2>
    <hr />
    <div class="card-body">
      <h6 class="card-subtitle mb-2 text-muted">
        <strong>작성자:</strong> {{ notice.username }} |
        <strong>작성일:</strong> {{ notice.postDate }}
      </h6>
      <p class="card-text mt-2" style="min-height: 300px">
        {{ notice.content }}
      </p>
    </div>
    <div class="d-flex justify-content-end mt-4">
      <button class="btn btn-secondary me-2" @click="$router.push('/post')">
        뒤로 가기
      </button>
      <button
        v-if="isEditable"
        class="btn btn-primary me-2"
        @click="moveToPage"
      >
        수정
      </button>
      <button v-if="isEditable" class="btn btn-danger" @click="onDelete">
        삭제
      </button>
    </div>
    <br />
    <CommentList />
  </div>
</template>
<script setup>
import { getNoticeById, deleteNotice } from '@/api/notices';
import { useRoute, useRouter } from 'vue-router';
import { ref, computed, onMounted } from 'vue';
import { useStorageStore, useToastStore } from '@/store/index';
import CommentList from '@/components/comment/CommentList.vue';

const route = useRoute();
const router = useRouter();
const storage = useStorageStore();
const toast = useToastStore();
const notice = ref({
  title: '',
  content: '',
  username: '',
  postDate: '',
  memberId: '',
});
const noticeId = route.params.id;

const getNotice = async () => {
  try {
    const { data } = await getNoticeById(noticeId);
    notice.value = data;
  } catch (error) {
    console.error(error);
  }
};

const isEditable = computed(() => {
  return storage.getUserId == notice.value.memberId;
});

const moveToPage = () => {
  router.push('/post/' + noticeId);
};

const onDelete = async () => {
  try {
    await deleteNotice(storage.getToken, noticeId);
    router.push('/post');
    toast.setToast('게시글이 삭제되었습니다.');
  } catch (error) {
    toast.setToast('게시글 삭제에 실패하였습니다.', 'danger');
  }
};

onMounted(getNotice);
</script>
<style></style>

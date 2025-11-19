<template>
  <div class="q-gutter-md q-mx-auto" style="max-width: 80%">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-md"><b>게시글 조회</b></h2>
      <div v-if="loading" class="text-center q-py-xl">
        <q-spinner color="primary" size="3em" />
        <div class="q-mt-md">데이터를 불러오는 중...</div>
      </div>
      <div v-else-if="error" class="text-center q-py-xl text-negative">
        {{ error }}
      </div>

      <q-card v-else-if="notice" flat bordered>
        <q-card-section class="bg-blue-grey-1 q-pa-lg">
          <div class="text-h5 text-weight-bold q-mb-sm">{{ notice.title }}</div>
          <div class="row items-center text-caption text-blue-grey-6">
            <q-icon name="person" size="16px" class="q-mr-xs" />
            <span class="q-mr-md">{{
              notice.authorName || '작성자 정보 없음'
            }}</span>
            <q-icon name="schedule" size="16px" class="q-mr-xs" />
            <span>{{ formattedDate(notice.postDate) }}</span>
          </div>
        </q-card-section>

        <q-separator />

        <q-card-section>
          <div
            v-if="notice.contentType === 'html'"
            v-html="notice.content"
            class="notice-content-html"
          ></div>
          <div v-else class="notice-content-text">
            {{ notice.content }}
          </div>
        </q-card-section>

        <q-separator />

        <q-card-actions align="right">
          <q-btn
            label="목록으로"
            flat
            @click="router.push({ path: '/notice/list' })"
          />
        </q-card-actions>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from 'boot/axios';

const route = useRoute();
const router = useRouter();
const noticeId = route.params.id; // URL 파라미터 게시글 ID
const notice = ref(null);
const loading = ref(true);
const error = ref(null);

// 날짜 포맷팅 함수
const formattedDate = dateString => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

// 게시글 데이터 불러오기
const fetchNotice = async () => {
  try {
    loading.value = true;
    error.value = null;
    const response = await api.get(`/notice/${noticeId}`);
    notice.value = response.data;
  } catch (err) {
    console.error(err);
    error.value = '게시글을 불러오는 데 실패했습니다.';
    notice.value = null;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchNotice();
});
</script>

<style lang="scss" scoped>
.notice-content-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.notice-content-html {
  // HTML이 안전하게 렌더링되도록 필요한 스타일 추가 (백엔드에서 반드시 HTML Sanitization 수행 필요)
  :deep(img) {
    max-width: 100%;
    height: auto;
  }
}
</style>

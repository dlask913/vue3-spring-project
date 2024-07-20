<template>
  <div class="container mt-5" style="width: 70%">
    <h1 class="mb-4">게시판</h1>
    <!-- 검색 기능 -->
    <SearchBar :searchOptions="searchOptions" @handle-search="searchNotices" />
    <hr />
    <table class="table table-hover text-center">
      <thead>
        <tr>
          <th scope="col">No.</th>
          <th scope="col" style="width: 70%">제목</th>
          <th scope="col">작성자</th>
          <th scope="col">작성일</th>
          <th scope="col">조회수</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(notice, index) in notices"
          :key="notice.id"
          @click="moveToPage(notice.id)"
        >
          <th scope="row">
            {{ params._limit * (params._page - 1) + index + 1 }}
          </th>
          <td>{{ notice.title }}</td>
          <td>{{ notice.username }}</td>
          <td>{{ notice.postDate }}</td>
          <td>{{ notice.viewCount }}</td>
        </tr>
      </tbody>
    </table>
    <div class="d-flex justify-content-end" v-if="storage.isLogin">
      <router-link to="/post/new" class="btn btn-primary"
        >새 글 작성</router-link
      >
    </div>
  </div>
  <!-- Pagination 추가 -->
  <Pagination
    :currentPage="params._page"
    :pageCount="pageCount"
    @page-changed="goToPage"
  />
</template>

<script setup>
import { getNotices, getNoticesByKeyword } from '@/api/notices';
import { ref, computed, onMounted } from 'vue';
import { useStorageStore } from '@/store';
import { useRouter } from 'vue-router';
import Pagination from '@/components/common/Pagination.vue';
import SearchBar from '@/components/common/SearchBar.vue';

const router = useRouter();
const storage = useStorageStore();
const notices = ref([]);
const searchOptions = ref([
  { key: 'title', value: '제목' },
  { key: 'username', value: '글쓴이' },
]);
const selectedOption = ref('title');
const searchValue = ref('');
const params = ref({
  _sort: 'createdAt',
  _order: 'desc',
  _page: 1,
  _limit: 5,
});
const totalCount = ref(1);

const fetchInit = async () => {
  try {
    const { data } = await getNotices(selectedOption.value, searchValue.value);
    totalCount.value = data.length || 1;
  } catch (error) {
    console.error(error);
  }
};

const fetchNotices = async () => {
  try {
    const { data } = await getNoticesByKeyword(
      params.value._page,
      params.value._limit,
      selectedOption.value,
      searchValue.value
    );
    notices.value = data;
  } catch (error) {
    console.error(error);
  }
};

const moveToPage = (noticeId) => {
  router.push('/post-details/' + noticeId);
};

onMounted(() => {
  fetchInit();
  fetchNotices();
});

const pageCount = computed(() =>
  Math.ceil(totalCount.value / params.value._limit)
);

const goToPage = (page) => {
  params.value._page = page;
  fetchNotices();
};

const searchNotices = async (option, value) => {
  selectedOption.value = option;
  searchValue.value = value;
  fetchInit();
  params.value._page = 1;
  try {
    const { data } = await getNoticesByKeyword(
      params.value._page,
      params.value._limit,
      selectedOption.value,
      searchValue.value
    );
    notices.value = data;
  } catch (error) {
    console.error(error);
  }
};
</script>

<style></style>

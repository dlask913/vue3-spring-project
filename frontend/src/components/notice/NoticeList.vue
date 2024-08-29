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
          <th
            @click="sortNotice('title')"
            scope="col"
            style="width: 60%"
            class="field-indicator"
          >
            제목 <i class="fas fa-sort sort-indicator" id="title-sort"></i>
          </th>
          <th scope="col" class="field-indicator">작성자</th>
          <th
            @click="sortNotice('post_date')"
            scope="col"
            class="field-indicator"
          >
            작성일 <i class="fas fa-sort sort-indicator" id="date-sort"></i>
          </th>
          <th
            @click="sortNotice('view_count')"
            scope="col"
            class="field-indicator"
          >
            조회수 <i class="fas fa-sort sort-indicator" id="view-sort"></i>
          </th>
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
          <td>
            {{ notice.title }}
            <span v-if="isNew(notice.postDate)" class="badge bg-warning ms-2"
              >NEW</span
            >
            <span v-if="isHot(notice.viewCount)" class="badge bg-danger ms-2"
              >HOT</span
            >
          </td>
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
  _sort: 'post_date',
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
      searchValue.value,
      params.value._sort,
      params.value._order
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
      searchValue.value,
      params.value._sort,
      params.value._order
    );
    notices.value = data;
  } catch (error) {
    console.error(error);
  }
};

const isNew = (postDate) => {
  const postTime = new Date(postDate).getTime();
  const currentTime = new Date().getTime();
  const timeDifference = currentTime - postTime;

  // 24시간(1일) 이내일 경우 NEW 표시
  return timeDifference <= 24 * 60 * 60 * 1000; // 24시간 = 24 * 60분 * 60초 * 1000밀리초
};

const isHot = (viewCount) => {
  return viewCount >= 10; // todo: 기준값 다시 정하기.
};

const sortNotice = (keyWord) => {
  // 정렬
  if (params.value._sort === keyWord) {
    params.value._order = params.value._order === 'asc' ? 'desc' : 'asc';
  } else {
    params.value._order = 'asc';
  }
  params.value._sort = keyWord;
  fetchNotices();
};
</script>

<style scoped>
.sort-indicator {
  color: gray;
}

.field-indicator:hover {
  cursor: pointer;
}
</style>

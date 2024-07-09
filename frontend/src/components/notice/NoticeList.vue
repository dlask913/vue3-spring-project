<template>
    <div class="container mt-5" style="width: 70%;">
        <h1 class="mb-4">게시판</h1>
        <!-- 검색 기능 -->
        <form class="d-flex" @submit.prevent="searchNotices">
          <div class="row w-100">
            <div class="col-3">
              <select class="form-select" v-model="searchOption">
                <option value="username">글쓴이</option>
                <option value="title">제목</option>
              </select>
            </div>
            <div class="col-7">
              <input class="form-control" type="search" placeholder="Search" aria-label="Search" v-model="searchValue">
            </div>
            <div class="col-2">
              <button class="btn btn-outline-success w-100" type="submit">검색하기</button>
            </div>
          </div>
        </form>
        <!-- -->
        <hr>
        <table class="table table-hover text-center">
            <thead>
                <tr>
                    <th scope="col">No.</th>
                    <th scope="col" style="width: 70%;">제목</th>
                    <th scope="col">작성자</th>
                    <th scope="col">작성일</th>
                </tr>   
            </thead>
            <tbody>
                <tr v-for="(notice, index) in notices" :key="notice.id" @click="moveToPage(notice.id)">
                    <th scope="row">{{ params._limit*(params._page-1) + index + 1 }}</th>
                    <td>{{ notice.title }}</td>
                    <td>{{ notice.username }}</td>
                    <td>{{ notice.postDate }}</td>
                </tr>
            </tbody>
        </table>
        <div class="d-flex justify-content-end" v-if="storage.isLogin">
            <router-link to="/post/new" class="btn btn-primary">새 글 작성</router-link>
        </div>
    </div>
    <!-- Pagination 추가 -->
    <Pagination 
      :currentPage="params._page"
      :pageCount="pageCount"
      @page-changed="goToPage"
    />
</template>

<script>

import { getNotices, getNoticesByKeyword } from '@/api/notices';
import { ref, computed } from 'vue';
import { useStorageStore } from '@/store';
import { useRouter } from 'vue-router';
import Pagination from '@/components/common/Pagination.vue';
export default {
    components: {
      Pagination,
    },
    setup() {
        const router = useRouter();
        const storage = useStorageStore();
        const notices = ref([]);
        const searchOption = ref('title'); // 검색 옵션
        const searchValue = ref(''); // 검색 키워드
        const params = ref({ // pagination
            _sort: 'createdAt',
            _order: 'desc',
            _page: 1,
            _limit: 5,
        });
        const totalCount = ref(1); // 게시글 전체 개수

        const fetchInit = async() => { // 게시글 totalCount 가져오기
          try {
              const { data } = await getNotices(searchOption.value, searchValue.value);
              totalCount.value = data.length == 0 ? 1 : data.length;
            } catch (error){
              console.error(error);
            }
        };

        const fetchNotices  = async ()  => { // 현재 page 의 게시글 데이터 가져오기 
            try {
                const { data } = await getNoticesByKeyword(params.value._page, params.value._limit, searchOption.value, searchValue.value);
                notices.value = data;
            } catch (error){
                console.error(error);
            }
        };

        const moveToPage = (noticeId) => { // 게시글 상세보기로 이동
            router.push('/post-details/' + noticeId);
        };

        fetchInit();
        fetchNotices();
        
        const pageCount = computed(() => // 최대 페이지 계산
            Math.ceil(totalCount.value / params.value._limit),
        );

        const goToPage = (page) => { // 페이지 이동
          params.value._page = page;
          fetchNotices();
        };

        const searchNotices = async () => { // 키워드 검색 (title or username)
          fetchInit(); // totalCount 초기화
          params.value._page = 1; // 검색 시 1 페이지로 설정
          try {
              const { data } = await getNoticesByKeyword(params.value._page, params.value._limit, searchOption.value, searchValue.value);
              notices.value = data;
          } catch (error){
              console.error(error);
          }
        };

        return {
            storage,
            notices,
            moveToPage,
            goToPage,
            params,
            totalCount,
            pageCount,
            searchOption,
            searchValue,
            searchNotices,
        }
    }
}
</script>

<style>
</style>
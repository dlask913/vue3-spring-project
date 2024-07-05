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
    <nav class="mt-5" aria-label="Page navigation example">
      <ul class="pagination justify-content-center">
        <li class="page-item">
          <a
            class="page-link"
            href="#"
            aria-label="Previous"
            @click.prevent="goToPage(params._page-1)"
          >
            <span aria-hidden="true">&laquo;</span>
          </a>
        </li>
        <li
          v-for="page in pageCount"
          :key="page"
          class="page-item"
          :class="{ active: params._page === page }"
        >
          <a class="page-link" href="#" @click.prevent="params._page = page">{{
            page
          }}</a>
        </li>
        <li class="page-item">
          <a
            class="page-link"
            href="#"
            aria-label="Next"
            @click.prevent="goToPage(params._page+1)"
          >
            <span aria-hidden="true">&raquo;</span>
          </a>
        </li>
      </ul>
    </nav>
</template>

<script>

import { getNotices, getNoticesByPage, getNoticesByKeyword } from '@/api/notices';
import { ref, computed, watchEffect } from 'vue';
import { useStorageStore } from '@/store';
import { useRouter } from 'vue-router';
export default {
    setup() {
        const router = useRouter();
        const storage = useStorageStore();
        const notices = ref([]);
        const searchOption = ref('title'); // 검색 옵션
        const searchValue = ref(''); // 검색 키워드

        // pagination
        const params = ref({
            _sort: 'createdAt',
            _order: 'desc',
            _page: 1,
            _limit: 5,
        });
        const totalCount = ref(0);

        const fetchInit = async() => { // 게시글 totalCount 가져오기
          try {
                const { data } = await getNotices();
                totalCount.value = data.length;
            } catch (error){
                console.error(error);
            }
        };

        const fetchNotices  = async ()  => { // 현재 page 에 따라 게시글 Pagination API 호출
            try {
                const { data } = await getNoticesByPage(params.value._page, params.value._limit);
                notices.value = data;
            } catch (error){
                console.error(error);
            }
        };

        const moveToPage = (noticeId) => { // 게시글 상세보기로 이동
            router.push('/post-details/' + noticeId);
        };

        fetchInit();
        watchEffect(fetchNotices);
        
        const pageCount = computed(() => // 최대 페이지 계산
            Math.ceil(totalCount.value / params.value._limit),
        );

        const goToPage = (page) => { // 페이지 이동
          if (page < 1 || page > pageCount.value) return; // 페이지 범위를 벗어나는 경우
          params.value._page = page;
        };

        const searchNotices = async () => { // 키워드 검색 (title or username)
          /** to do
           * total size 다시 계산하기 
           */
          if (!searchValue.value){
            fetchInit();
            return;
          }
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
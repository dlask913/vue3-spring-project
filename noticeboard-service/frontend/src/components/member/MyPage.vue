<template>
  <div class="d-flex">
    <!-- 사이드바 -->
    <div
      class="d-flex flex-column flex-shrink-0 p-3 mt-5 ms-5"
      style="width: 280px;"
    >
      <div
        class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none"
      >
        <span class="fs-4">MyPage</span>
      </div>
      <hr />
      <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
          <a href="#" class="nav-link active" aria-current="page"> Home </a>
        </li>
        <li>
          <a href="#" class="nav-link link-dark"> Posts </a>
        </li>
        <li>
          <a href="#" class="nav-link link-dark"> Comments </a>
        </li>
        <li>
          <a href="#" class="nav-link link-dark"> Messages </a>
        </li>
      </ul>
    </div>

    <!-- 메인 콘텐츠 -->
    <div class="container mt-5 me-5" style="">
      <div class="row">
        <div class="col-md-4 text-center">
          <ImageUploader
            v-if="member.imgUrl"
            :imageUrl="member.imgUrl"
            @file-changed="fetchMemberImg"
          />
          <h4 class="text-muted">{{ member.username }}</h4>
        </div>
        <div class="col-md-6">
          <h3>Personal Information</h3>
          <form @submit.prevent="onUpdateUser">
            <div class="mb-3">
              <label for="email" class="form-label">Email address</label>
              <input
                v-model="member.email"
                type="email"
                class="form-control"
                id="email"
                disabled
              />
            </div>
            <div class="mb-3">
              <label for="phone" class="form-label">Username</label>
              <input
                v-model="member.username"
                type="text"
                class="form-control"
                id="username"
              />
            </div>
            <div class="mb-3">
              <label for="address" class="form-label">Address</label>
              <input
                v-model="member.address"
                type="text"
                class="form-control"
                id="address"
                placeholder="1234 Main St"
              />
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
          </form>
        </div>
      </div>

      <div class="row mt-5">
        <div class="col-md-5">
          <h3>My Posts</h3>
          <div
            class="card mb-3"
            v-for="notice in notices"
            :key="notice.id"
            @click="moveToPage(notice.id)"
            :style="{ cursor: isActive === notice.id ? 'default' : 'pointer' }"
          >
            <div class="card-body">
              {{ notice.id }}
              <h5 class="card-title">{{ notice.title }}</h5>
              <p class="card-text">{{ notice.content }}</p>
              <p class="card-text">
                <small class="text-muted"
                  >Posted on {{ notice.postDate }}</small
                >
              </p>
            </div>
          </div>
        </div>
        <div class="col-md-5">
          <h3>My Comments</h3>
          <div class="card mb-3" v-for="comment in comments" :key="comment.id">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <p class="card-text">{{ comment.content }}</p>
                <button
                  class="btn btn-secondary btn-sm mt-2"
                  @click="moveToPage(comment.noticeId)"
                >
                  게시글 보기
                </button>
              </div>
              <p class="card-text">
                <small class="text-muted"
                  >Commented on {{ comment.postDate }}</small
                >
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import ImageUploader from '../common/ImageUploader.vue'
import { ref, onMounted } from 'vue'
import { getMemberById, updateMember } from '@/api/users'
import { getNoticesByMember } from '@/api/notices'
import { getCommentsByMember } from '@/api/comments'
import { useToastStore, useStorageStore } from '@/store'
import { useRouter } from 'vue-router'

const router = useRouter()
const toast = useToastStore()
const storage = useStorageStore()
const member = ref({
  email: '',
  username: '',
  address: '',
  imgUrl: '',
})
const memberImg = ref(null)
const notices = ref([])
const comments = ref([])
const isActive = ref(null)

const fetchData = async () => {
  try {
    const [memberResponse, noticesResponse, commentsResponse] =
      await Promise.all([
        getMemberById(storage.getToken, storage.getUserId),
        getNoticesByMember(storage.getToken),
        getCommentsByMember(storage.getToken),
      ])
    member.value = memberResponse.data
    notices.value = noticesResponse.data
    comments.value = commentsResponse.data
    member.value.imgUrl = 'http://localhost:8080' + member.value.imgUrl // todo: baseUrl 따로 빼기
  } catch (e) {
    console.error(e.response.data)
  }
}

const onUpdateUser = async () => {
  try {
    await updateMember(
      storage.getToken,
      storage.getUserId,
      member.value,
      memberImg,
    )
    toast.setToast('정보 수정이 완료되었습니다.')
  } catch (e) {
    console.error(e.response.data)
  }
}

const fetchMemberImg = file => {
  memberImg.value = file
}

const moveToPage = noticeId => {
  isActive.value = noticeId
  router.push('/post-details/' + noticeId)
}

onMounted(fetchData)
</script>
<style scoped>
/* 전체 layout을 가로로 배치 */
.d-flex {
  display: flex;
  flex-direction: row;
}

.profile-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 20px;
}
</style>

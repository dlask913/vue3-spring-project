<template>
  <div class="d-flex">
    <!-- 사이드바 -->
    <div
      class="d-flex flex-column flex-shrink-0 p-3 mt-5 ms-5"
      style="width: 280px"
    >
      <div
        class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none"
      >
        <span class="fs-4">MyPage</span>
      </div>
      <hr />
      <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
          <a
            href="#"
            class="nav-link"
            :class="{ active: selectedMenu === 'home' }"
            @click.prevent="selectedMenu = 'home'"
          >
            Home
          </a>
        </li>
        <li>
          <a
            href="#"
            class="nav-link link-dark"
            :class="{ active: selectedMenu === 'posts' }"
            @click.prevent="selectedMenu = 'posts'"
          >
            Posts
          </a>
        </li>
        <li>
          <a
            href="#"
            class="nav-link link-dark"
            :class="{ active: selectedMenu === 'comments' }"
            @click.prevent="selectedMenu = 'comments'"
          >
            Comments
          </a>
        </li>
        <li>
          <a
            href="#"
            class="nav-link link-dark"
            :class="{ active: selectedMenu === 'messages' }"
            @click.prevent="selectedMenu = 'messages'"
          >
            Messages
          </a>
        </li>
      </ul>
    </div>

    <!-- 메인 콘텐츠 -->
    <div class="container mt-5 me-5" style="">
      <div class="row" v-if="selectedMenu === 'home'">
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

      <!-- 내가 작성한 게시물 -->
      <div class="row mt-5" v-if="selectedMenu === 'posts'">
        <h3 class="mb-3">My Posts</h3>
        <div class="col-md-5">
          <div
            class="card mb-3"
            v-for="notice in notices"
            :key="notice.id"
            @click="moveToNoticePage(notice.id)"
            :style="{ cursor: isActive === notice.id ? 'default' : 'pointer' }"
          >
            <div class="card-body">
              <h5 class="card-title mt-1">{{ notice.title }}</h5>
              <p class="card-text">{{ notice.content }}</p>
              <p class="card-text">
                <small class="text-muted"
                  >{{ notice.postType }}, posted on {{ notice.postDate }}</small
                >
              </p>
            </div>
          </div>
        </div>
        <div class="col-md-5">
          <!-- Post Type is PRODUCT -->
          <div
            class="card mb-3"
            v-for="product in products"
            :key="product.id"
            @click="moveToProductPage(product.id)"
            :style="{ cursor: isActive === product.id ? 'default' : 'pointer' }"
          >
            <div class="card-body">
              <h5 class="card-title mt-1">{{ product.title }}</h5>
              <p class="card-text">{{ product.content }}</p>
              <p class="card-text">
                <small class="text-muted"
                  >{{ product.postType }}, posted on
                  {{ product.postDate }}</small
                >
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- 내가 작성한 댓글 -->
      <div class="row mt-5" v-if="selectedMenu === 'comments'">
        <div class="col-md-5">
          <h3>My Comments</h3>
          <div class="card mb-3" v-for="comment in comments" :key="comment.id">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <p class="card-text">{{ comment.content }}</p>
                <button
                  class="btn btn-secondary btn-sm mt-2"
                  @click="moveToNoticePage(comment.noticeId)"
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

      <!-- 나의 채팅방 -->
      <div class="row mt-5" v-if="selectedMenu === 'messages'">
        <div class="col-md-5">
          <h3>Messages</h3>
          <div class="card mb-3" v-for="room in rooms" :key="room.id">
            <div
              class="card-body"
              @click="openRoom(room)"
              style="cursor: pointer"
            >
              <div class="d-flex justify-content-between">
                <p class="card-text">
                  {{ truncateMessage(room.latestMessage) }}
                </p>
              </div>
              <p class="card-text">
                <small class="text-muted">with {{ room.otherUsername }}</small>
              </p>
            </div>
            <i
              v-if="room.isRead !== 'Y'"
              class="bi bi-exclamation-circle-fill text-danger position-absolute top-0 end-0 m-2"
            ></i>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 채팅방 팝업 -->
  <RoomPopup
    v-if="isRoomOpen"
    :room="selectedRoom"
    @close="isRoomOpen = false"
  />
</template>

<script setup>
import ImageUploader from '../common/ImageUploader.vue'
import RoomPopup from '../common/RoomPopup.vue'
import { ref, onMounted } from 'vue'
import { getMemberById, updateMember } from '@/api/users'
import { getNoticesByMember } from '@/api/notices'
import { getCommentsByMember } from '@/api/comments'
import { updateReadStatus, getRoomsByMemberId } from '@/api/messages'
import { getProductsByMemberId } from '@/api/products'
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
const products = ref([])
const comments = ref([])
const rooms = ref([]) // 내 채팅방들
const isActive = ref(null)
const selectedMenu = ref('home') // 현재 선택된 메뉴
const selectedRoom = ref('')
const isRoomOpen = ref(false) // 채팅방 열기 (RoomPopup)

const fetchData = async () => {
  try {
    const [
      memberResponse,
      noticesResponse,
      commentsResponse,
      roomsResponse,
      productsResponse,
    ] = await Promise.all([
      getMemberById(storage.getToken, storage.getUserId),
      getNoticesByMember(storage.getToken),
      getCommentsByMember(storage.getToken),
      getRoomsByMemberId(storage.getToken, storage.getUserId),
      getProductsByMemberId(storage.getToken, storage.getUserId),
    ])
    member.value = memberResponse.data
    notices.value = noticesResponse.data
    products.value = productsResponse.data
    comments.value = commentsResponse.data
    rooms.value = roomsResponse.data.map(room => ({
      ...room,
      otherUserId:
        room.lowerId == storage.getUserId ? room.higherId : room.lowerId, // 상대방 id
      otherUsername:
        room.lowerId == storage.getUserId
          ? room.higherIdUsername
          : room.lowerIdUsername, // 상대방 username
    }))
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

const moveToNoticePage = noticeId => {
  isActive.value = noticeId
  router.push('/post-details/' + noticeId)
}

const moveToProductPage = productId => {
  isActive.value = productId
  router.push('/product-details/' + productId)
}

// 메시지 내용이 20자 이상이면 "..." 붙이기
const truncateMessage = message => {
  return message.length > 20 ? message.slice(0, 20) + '...' : message
}

// 메시지 클릭 시 팝업 열기
const openRoom = async room => {
  selectedRoom.value = room
  isRoomOpen.value = true
  if (room.isRead === 'N') {
    try {
      await updateReadStatus(
        storage.getToken,
        storage.getUserId,
        room.otherUserId,
      ) // room 내 상대방 메시지에 대한 isRead update로
      room.isRead = 'Y'
    } catch (e) {
      console.error(e.response.data)
    }
  }
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

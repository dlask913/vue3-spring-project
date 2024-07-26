<template>
  <div class="container mt-5">
    <div class="row">
      <div class="col-md-4 text-center">
        <h2 class="mt-3">Username</h2>
        <p class="text-muted">{{ member.username }}</p>
      </div>
      <div class="col-md-8">
        <h3>Personal Information</h3>
        <form>
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
      <div class="col-md-6">
        <h3>My Posts</h3>
        <div class="card mb-3" v-for="notice in notices" :key="notice.id">
          <div class="card-body">
            <h5 class="card-title">{{ notice.title }}</h5>
            <p class="card-text">{{ notice.content }}</p>
            <p class="card-text">
              <small class="text-muted">Posted on {{ notice.postDate }}</small>
            </p>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <h3>My Comments</h3>
        <div class="card mb-3" v-for="comment in comments" :key="comment.id">
          <div class="card-body">
            <p class="card-text">{{ comment.content }}</p>
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
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { getMemberById } from '@/api/users';
import { getNoticesByMember } from '@/api/notices';
import { getComentsByMember } from '@/api/comments';
import { useStorageStore } from '@/store';

const storage = useStorageStore();
const member = ref({
  email: '',
  username: '',
  address: '',
});
const notices = ref([]);
const comments = ref([]);

const fetchData = async () => {
  try {
    const [memberResponse, noticesResponse, commentsResponse] =
      await Promise.all([
        getMemberById(storage.getUserId),
        getNoticesByMember(storage.getToken),
        getComentsByMember(storage.getToken),
      ]);
    member.value = memberResponse.data;
    notices.value = noticesResponse.data;
    comments.value = commentsResponse.data;
  } catch (e) {
    console.error(e);
  }
};

onMounted(fetchData);

</script>
<style scoped>
.profile-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 20px;
}
</style>

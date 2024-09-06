<template>
  <ul class="list-group mt-1">
    <hr />
    <li
      v-for="reply in replies"
      :key="reply.id"
      class="list-group-item replies"
    >
      <div>
        <div class="comment-header">
          <img src="" alt="Profile Picture" class="profile-picture" />
          <div class="fw-bold mt-1 mb-2">{{ reply.username }}</div>
        </div>
        <span>{{ reply.content }}</span>
        <p class="fw-lighter mt-2">{{ reply.postDate }}</p>
      </div>
      <hr />
    </li>
  </ul>
</template>

<script setup>
import { ref } from 'vue';

const replies = ref([
  {
    username: 'username',
    content: 'content1',
    postDate: '대댓글 날짜',
  },
  {
    username: 'username',
    content: 'content1',
    postDate: '대댓글 날짜',
  },
]);
</script>

<style scoped>
.replies {
  display: flex;
  border: none;
  border-bottom: 1px solid #ddd;
  margin: 0 auto;
  width: 80%;
}

.replies > span {
  margin-right: 10px;
}

.replies:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
}

.profile-picture {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}
</style>

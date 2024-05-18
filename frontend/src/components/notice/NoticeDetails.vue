<template>
  <div class="container mt-5">
    <h1 class="mb-3">{{ notice.title }}</h1>
    <div class="mb-1">
      <strong>작성자:</strong> {{ notice.username }}
    </div>
    <div class="mb-1">
      <strong>작성일:</strong> {{ notice.postDate }}
    </div>
    <div class="mt-3 mb-3">
      <p>{{ notice.content }}</p>
    </div>
    <button class="btn btn-secondary me-3" @click="$router.push('/post')">뒤로 가기</button>
    <button v-if="isEditable" class="btn btn-primary" @click="moveToPage()">수정하기</button>
  </div>
</template>
<script>
import axios from '@/axios';
import { useRoute, useRouter } from 'vue-router';
import { ref, computed } from 'vue';
import { useStorageStore } from '@/store/index';
export default {
  setup(){
    const route = useRoute();
    const router = useRouter();
    const storage = useStorageStore();
    const notice = ref({
      title: '',
      content: '',
      username: '',
      postDate: '',
      memberId: ''
    })
    const noticeId = route.params.id;
    const getNotice = async () => {
      try{
        let res = await axios.get(`notice/${noticeId}`);
        notice.value = { ...res.data };
      } catch(error){
        console.log(error);
      }
    };

    const isEditable = computed(() => {
      return storage.getUserId === notice.value.memberId;
    });

    const moveToPage = () => {
      router.push('/post/' + noticeId);
    };

    getNotice();

    return {
      notice,
      isEditable,
      moveToPage,
    }
  }
}
</script>
<style>
  
</style>
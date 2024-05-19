<template>
  <div class="container mt-5" style="width: 60%;">
        <div class="card">
            <div class="card-header">
                <h2>{{ notice.title }}</h2>
            </div>
            <div class="card-body">
                <h6 class="card-subtitle mb-2 text-muted">
                    <strong>작성자:</strong> {{ notice.username }} | <strong>작성일:</strong> {{ notice.postDate }}
                </h6>
                <p class="card-text mt-2" style="min-height: 300px;">{{ notice.content }}</p>
            </div>
            
        </div>
        <div class="d-flex justify-content-end mt-4">
            <button class="btn btn-secondary me-2" @click="$router.push('/post')">뒤로 가기</button>
            <button v-if="isEditable" class="btn btn-primary me-2" @click="moveToPage">수정</button>
            <button v-if="isEditable" class="btn btn-danger" @click="onDelete">삭제</button>
        </div>
    </div>
</template>
<script>
import axios from '@/axios';
import { useRoute, useRouter } from 'vue-router';
import { ref, computed } from 'vue';
import { useStorageStore, useToastStore } from '@/store/index';
export default {
  setup(){
    const route = useRoute();
    const router = useRouter();
    const storage = useStorageStore();
    const toast = useToastStore();
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

    const onDelete = async () => {
      try{
        let res = await axios.delete(`notice/${noticeId}`,{
          headers: {'Authorization': storage.getToken, }
        });
        router.push('/post');
        toast.setToast('게시글이 삭제되었습니다.');
      } catch(error){
        toast.setToast('게시글 삭제에 실패하였습니다.','danger');
      }
    };

    getNotice();

    return {
      notice,
      isEditable,
      moveToPage,
      onDelete,
    }
  }
}
</script>
<style>
  
</style>
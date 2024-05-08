<template>

<div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="confirmModalLabel">알림</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <span>{{ message }}</span>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
        <button type="button" class="btn btn-primary" @click="onQuit" data-bs-dismiss="modal">확인</button>
      </div>
    </div>
  </div>
</div>
</template>
ㄴ
<script>
import axios from '@/axios';
import { useRouter } from 'vue-router';
import { useStorageStore, useToastStore } from '@/store/index';
export default {
    props: {
        message: {
            type: String,
            required: true
        },
    },
    setup(){
        const router = useRouter();
        const storage = useStorageStore();
        const toast = useToastStore();
        const onQuit = async () => {
            try{
                let res;
                let memberId = storage.getUserId;
                let token = storage.getToken;

                res = await axios.delete(`member/${memberId}`,
                    {
                        headers: {'Authorization': token, }
                    }
                );
                storage.logout();
                toast.setToast('회원 탈퇴 완료');
            }catch(error){
                console.log(error);
                toast.setToast('재시도 부탁드립니다.','danger');
            }
        };

        return {
            onQuit
        }
    }
}
</script>
<style scoped>
</style>
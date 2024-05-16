<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <router-link class="navbar-brand" :to="{name: 'Home'}">Home</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">                
                    <li class="nav-item">
                        <router-link class="nav-link" :to="{name: 'NoticeList'}">게시판</router-link>
                    </li>
                </ul>
            </div>
            <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
                <ul class="navbar-nav">                
                    <li v-if="storage.getToken" class="nav-item">
                        <button class="nav-link" @click="onLogout">로그아웃</button>
                    </li>
                    <li v-if="storage.getToken" class="nav-item">
                        <button class="nav-link" @click="openModal" data-bs-toggle="modal" data-bs-target="#confirmModal">회원탈퇴</button>
                    </li>
                    <div v-else class="navbar-nav">
                        <router-link class="nav-link" :to="{name: 'RegisterForm'}">회원가입</router-link>
                        <router-link class="nav-link" :to="{name: 'LoginForm'}">로그인</router-link>
                    </div>
                </ul>
            </div>
        </div>
    </nav>
    <Modal 
        v-show="showModal" 
        :message="showMessage"
    />
</template>

<script>

import { useRouter } from 'vue-router';
import { ref } from 'vue';
import { useStorageStore, useToastStore } from '@/store/index';
import Modal from '@/components/common/Modal.vue';
export default {
    components:{
        Modal
    },
    setup(){
        const router = useRouter();
        const storage = useStorageStore();
        const toast = useToastStore();
        const showModal = ref(false);
        const showMessage = ref('');
        const onLogout= () => {
            storage.logout();
            router.push('/login');
        };
        const openModal = () => {
            showModal.value = true;
            showMessage.value = '정말 탈퇴하시겠습니까?';
        };

        return {
            storage,
            onLogout,
            openModal,
            showModal,
            showMessage,
        }
    }
}
</script>

<style>

</style>
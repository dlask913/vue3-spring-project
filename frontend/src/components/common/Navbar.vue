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
                        <a class="nav-link" href="#">게시판</a>
                    </li>
                    <li v-if="storage.getToken" class="nav-item">
                        <button class="nav-link" :to="{name: 'UpdateForm'}">마이페이지</button>
                    </li>
                    <li v-if="storage.getToken" class="nav-item">
                        <button class="nav-link" @click="onLogout">로그아웃</button>
                    </li>
                    <div v-else class="navbar-nav">
                        <router-link class="nav-link" :to="{name: 'RegisterForm'}">회원가입</router-link>
                        <router-link class="nav-link" :to="{name: 'LoginForm'}">로그인</router-link>
                    </div>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>

import { useRouter } from 'vue-router';
import { useStorageStore } from '@/store/index';
export default {
    setup(){
        const router = useRouter();
        const storage = useStorageStore();
        const onLogout= () => {
            storage.logout();
            router.push('/login');
        };

        return {
            storage,
            onLogout
        }
    }
}
</script>

<style>

</style>
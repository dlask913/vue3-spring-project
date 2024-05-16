<template>
    <div class="container mt-5" style="width: 70%;">
        <h1 class="mb-4">rptlvks</h1>
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
                <tr v-for="(notice, index) in notices" :key="notice.id">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ notice.title }}</td>
                    <td>{{ notice.username }}</td>
                    <td>{{ notice.postDate }}</td>
                </tr>
            </tbody>
        </table>
        <div class="d-flex justify-content-end" v-if="storage.getToken">
            <router-link to="/post/new" class="btn btn-primary">새 글 작성</router-link>
        </div>
    </div>
</template>

<script>
import axios from '@/axios';
import { ref } from 'vue';
import { useStorageStore } from '@/store';
export default {
    setup() {
        const storage = useStorageStore();
        const notices = ref([]);
        const getNotices  = async ()  => {
            try {
                let res = await axios.get('notices');
                notices.value = res.data;
            } catch (err){
                console.log(err);
            }
        };

        getNotices();

        return {
            storage,
            notices,
        }
    }
}
</script>

<style>
</style>
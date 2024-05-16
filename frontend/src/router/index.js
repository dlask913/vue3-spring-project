import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/components/common/Home.vue';
import RegisterForm from '@/components/member/RegisterForm.vue';
import LoginForm from '@/components/member/LoginForm.vue';
import NoticeList from '@/components/notice/NoticeList.vue';
import NoticeForm from '@/components/notice/NoticeForm.vue';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: Home
        },
        {
            path: '/register',
            name: 'RegisterForm',
            component: RegisterForm
        },
        {
            path: '/login',
            name: 'LoginForm',
            component: LoginForm
        },
        {
            path: '/post',
            name: 'NoticeList',
            component: NoticeList
        },
        {
            path: '/post/new',
            name: 'NoticeForm',
            component: NoticeForm
        }
    ]
});

export default router;
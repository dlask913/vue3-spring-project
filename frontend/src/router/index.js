import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/components/common/Home.vue';
import RegisterForm from '@/components/member/RegisterForm.vue';
import LoginForm from '@/components/member/LoginForm.vue';

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
        }
    ]
});

export default router;
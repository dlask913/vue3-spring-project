import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/components/Home.vue';
import RegisterForm from '@/components/RegisterForm.vue';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: Home
        }
    ]
});

export default router;
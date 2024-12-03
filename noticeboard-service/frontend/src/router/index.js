import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/components/common/Home.vue'
import RegisterForm from '@/components/member/RegisterForm.vue'
import LoginForm from '@/components/member/LoginForm.vue'
import NoticeList from '@/components/notice/NoticeList.vue'
import NoticeForm from '@/components/notice/NoticeForm.vue'
import NoticeDetails from '@/components/notice/NoticeDetails.vue'
import NoticeEdit from '@/components/notice/NoticeEdit.vue'
import MyPage from '@/components/member/MyPage.vue'
import ProductForm from '@/components/product/ProductForm.vue'
import ProductList from '@/components/product/ProductList.vue'
import ProductDetails from '@/components/product/ProductDetails.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
    },
    {
      path: '/register',
      name: 'RegisterForm',
      component: RegisterForm,
    },
    {
      path: '/login',
      name: 'LoginForm',
      component: LoginForm,
    },
    {
      path: '/post',
      name: 'NoticeList',
      component: NoticeList,
    },
    {
      path: '/post/new',
      name: 'NoticeForm',
      component: NoticeForm,
    },
    {
      path: '/post-details/:id',
      name: 'NoticeDetails',
      component: NoticeDetails,
    },
    {
      path: '/post-edit/:id',
      name: 'NoticeEdit',
      component: NoticeEdit,
    },
    {
      path: '/my-page',
      name: 'MyPage',
      component: MyPage,
    },
    {
      path: '/product/new',
      name: 'ProductForm',
      component: ProductForm,
    },
    {
      path: '/product',
      name: 'ProductList',
      component: ProductList,
    },
    {
      path: '/product-details/:id',
      name: 'ProductDetails',
      component: ProductDetails,
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    // 페이지 이동 시 항상 스크롤을 상단으로 설정
    return { top: 0 }
  },
})

export default router

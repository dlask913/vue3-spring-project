import { defineRouter } from '#q-app/wrappers';
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory,
} from 'vue-router';
import routes from './routes';
import { useUserStore } from 'stores/user';
import { Notify } from 'quasar';

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

export default defineRouter(function (/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === 'history'
      ? createWebHistory
      : createWebHashHistory;

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  // 네비게이션 가드 시작
  Router.beforeEach((to, from, next) => {
    const userStore = useUserStore();

    const isLoggedIn = userStore.isLoggedIn;
    const userRole = userStore.userRole;
    console.log(to.fullPath);

    // 로그인이 필요한 경우 -> 인증 실패
    if (to.meta.requiresAuth && !isLoggedIn) {
      return next({ path: '/login', query: { redirect: to.fullPath } });
    }

    // 로그인 이후, 권한이 없는 경우 - 403
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      Notify.create({
        type: 'negative',
        message: '접근 권한이 없습니다.',
        position: 'top',
        timeout: 2000,
      });
      return next({ name: 'AccessDeniedPage' });
    }

    // 이미 로그인 한 경우
    if (isLoggedIn && (to.path === '/login' || to.path === '/signup')) {
      return next({ path: '/' });
    }

    next(); // 모든 조건 통과 시 목적지로 이동
  });

  return Router;
});

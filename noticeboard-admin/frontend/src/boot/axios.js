import { defineBoot } from '#q-app/wrappers';
import axios from 'axios';
import { useUserStore } from 'stores/user';

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000, // 요청 타임아웃 설정
});

// 요청 인터셉터
api.interceptors.request.use(config => {
  const userStore = useUserStore();
  if (userStore.accessToken) {
    config.headers.Authorization = `Bearer ${userStore.accessToken}`;
  }
  return config;
});

export default defineBoot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export { api };

import { defineBoot } from '#q-app/wrappers';
import axios from 'axios';
import { useUserStore } from 'stores/user';
import { installAuthInterceptors } from '../api/authInterceptors.js';

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

export default defineBoot(({ router, store }) => {
  // 인터셉터 없는 별도 클라이언트 전달
  const refreshClient = axios.create({
    baseURL: api.defaults.baseURL,
    timeout: api.defaults.timeout,
  });
  installAuthInterceptors(api, {
    refreshClient,
    userStore: useUserStore(store),
    router,
  });
});

export { api };

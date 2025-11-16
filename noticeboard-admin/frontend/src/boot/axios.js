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

export default defineBoot(({ router, store }) => {
  // 요청 인터셉터
  api.interceptors.request.use(config => {
    const userStore = useUserStore(store);
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  });

  // 응답 인터셉터
  api.interceptors.response.use(
    response => response,
    async error => {
      const userStore = useUserStore();
      const originalRequest = error.config;

      // access token 만료로 403 발생한 경우
      if (error.response?.status === 403 && userStore.refreshToken) {
        try {
          // refresh API 호출
          const newAccessToken = await axios.post(
            'http://localhost:8081/refresh',
            {
              username: userStore.username,
              refreshToken: userStore.refreshToken,
            },
          );

          if (!newAccessToken.data) {
            console.warn('Refresh token response is invalid');
            userStore.clearAuthInfo();
            router.push('/login');
            return Promise.reject(error);
          }

          // 새로운 access token 저장
          userStore.setAuthInfo(
            userStore.userId,
            userStore.username,
            newAccessToken.data,
            userStore.refreshToken,
            userStore.email,
          );

          // 실패했던 요청에 새 토큰 넣어서 재시도
          originalRequest.headers.Authorization = `Bearer ${newAccessToken.data}`;
          return api(originalRequest);
        } catch (e) {
          console.error(e);

          // refreshToken 만료 → 로그아웃 처리
          userStore.clearAuthInfo();
          router.push('/login');
        }
      } else if (error.response?.status === 403) {
        // 토큰이 없거나 다른 이유로 403 발생 → 로그아웃 처리
        userStore.clearAuthInfo();
        router.push('/login');
      }

      return Promise.reject(error);
    },
  );
});

export { api };

import { CanceledError } from 'axios';

export function installAuthInterceptors(
  api,
  { refreshClient, userStore, router },
) {
  // 같은 로그인 상태에서 발생한 만료 요청들이 하나의 갱신 결과를 공유
  let refreshPromise = null;

  const refreshAccessToken = () => {
    if (!refreshPromise) {
      const username = userStore.username;
      const accessToken = userStore.accessToken;
      const isSameLogin = () =>
        userStore.username === username &&
        userStore.accessToken === accessToken;

      // 토큰 갱신 → 인터셉터 없는 refreshClient로 호출
      refreshPromise = refreshClient
        .post('/refresh', {}, { withCredentials: true })
        .then(({ data }) => {
          if (!isSameLogin()) {
            throw new CanceledError(
              '로그인 상태가 변경되어 토큰 갱신을 취소했습니다.',
            );
          }
          if (typeof data !== 'string' || !data.trim()) {
            throw new Error('Access Token 갱신 응답이 올바르지 않습니다.');
          }
          userStore.setAuthInfo(
            userStore.userId,
            username,
            data,
            userStore.email,
            userStore.userRole,
          );
          return data;
        })
        .catch(error => {
          // 공유 Promise 안에서 한 번만 처리
          if (error.response?.status === 401 && isSameLogin()) {
            userStore.clearAuthInfo();
            router.push('/login');
          }
          throw error;
        })
        .finally(() => {
          refreshPromise = null;
        });
    }
    return refreshPromise;
  };

  // 요청 인터셉터 등록 : 토큰이 있으면 Authorization 헤더에 추가
  api.interceptors.request.use(config => {
    config._authUsername = userStore.username;
    config._accessToken = userStore.accessToken;
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  });

  // 응답 인터셉터 등록 : 토큰 만료 시 갱신 후 재시도
  api.interceptors.response.use(
    response => response,
    async error => {
      const originalRequest = error.config;
      if (error.response?.status === 403) {
        if (error.response.data?.message === 'ACCESS_DENIED') {
          router.push('/error/denied');
        } else {
          userStore.clearAuthInfo();
          router.push('/login');
        }
        throw error;
      }

      if (
        error.response?.status !== 401 ||
        error.response?.data?.errorCode !== 'TOKEN_EXPIRED' ||
        !originalRequest ||
        originalRequest._retry ||
        !userStore.accessToken ||
        originalRequest._authUsername !== userStore.username
      ) {
        throw error;
      }

      originalRequest._retry = true;
      // 갱신 완료 후 늦게 도착한 이전 토큰의 만료 응답은 현재 토큰으로 재시도
      const accessToken =
        originalRequest._accessToken === userStore.accessToken
          ? await refreshAccessToken()
          : userStore.accessToken;

      if (
        originalRequest._authUsername !== userStore.username ||
        accessToken !== userStore.accessToken
      ) {
        throw new CanceledError(
          '로그인 상태가 변경되어 요청 재시도를 취소했습니다.',
        );
      }
      originalRequest.headers.Authorization = `Bearer ${accessToken}`;
      return api(originalRequest);
    },
  );
}

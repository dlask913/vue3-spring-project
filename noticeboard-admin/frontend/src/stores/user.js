import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    accessToken: null,
    refreshToken: null,
  }),
  getters: {
    getUserId: state => state.userId,
    getAccessToken: state => state.accessToken,
    getRefreshToken: state => state.refreshToken,
    isLoggedIn: state => !!state.accessToken,
  },
  actions: {
    setAuthInfo(userId, accessToken, refreshToken) {
      this.userId = userId;
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
    },
    clearAuthInfo() {
      this.userId = null;
      this.accessToken = null;
      this.refreshToken = null;
    },
  },
});

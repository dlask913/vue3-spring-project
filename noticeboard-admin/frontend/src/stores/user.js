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
      localStorage.setItem('userId', userId);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    },
    loadAuthInfo() {
      this.userId = localStorage.getItem('userId');
      this.accessToken = localStorage.getItem('accessToken');
      this.refreshToken = localStorage.getItem('refreshToken');
    },
    clearAuthInfo() {
      this.userId = null;
      this.accessToken = null;
      this.refreshToken = null;
      localStorage.removeItem('userId');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    },
  },
});

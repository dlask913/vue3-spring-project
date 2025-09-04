import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    accessToken: null,
    refreshToken: null,
    username: null,
  }),
  getters: {
    getUserId: state => state.userId,
    getAccessToken: state => state.accessToken,
    getRefreshToken: state => state.refreshToken,
    getUsername: state => state.username,
    isLoggedIn: state => !!state.accessToken,
  },
  actions: {
    setAuthInfo(userId, username, accessToken, refreshToken) {
      this.userId = userId;
      this.username = username;
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      localStorage.setItem('userId', userId);
      localStorage.setItem('username', username);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    },
    loadAuthInfo() {
      this.userId = localStorage.getItem('userId');
      this.username = localStorage.getItem('username');
      this.accessToken = localStorage.getItem('accessToken');
      this.refreshToken = localStorage.getItem('refreshToken');
    },
    clearAuthInfo() {
      this.userId = null;
      this.username = null;
      this.accessToken = null;
      this.refreshToken = null;
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    },
  },
});

import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    accessToken: null,
    refreshToken: null,
    username: null,
    email: null,
    userRole: null,
  }),
  getters: {
    getUserId: state => state.userId,
    getAccessToken: state => state.accessToken,
    getRefreshToken: state => state.refreshToken,
    getUsername: state => state.username,
    getEmail: state => state.email,
    getUserRole: state => state.userRole,
    isLoggedIn: state => !!state.accessToken,
  },
  actions: {
    setAuthInfo(userId, username, accessToken, refreshToken, email, userRole) {
      this.userId = userId;
      this.username = username;
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.email = email;
      this.userRole = userRole;
      localStorage.setItem('userId', userId);
      localStorage.setItem('username', username);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('email', email);
      localStorage.setItem('userRole', userRole);
    },
    loadAuthInfo() {
      this.userId = localStorage.getItem('userId');
      this.username = localStorage.getItem('username');
      this.accessToken = localStorage.getItem('accessToken');
      this.refreshToken = localStorage.getItem('refreshToken');
      this.email = localStorage.getItem('email');
      this.userRole = localStorage.getItem('userRole');
    },
    clearAuthInfo() {
      this.userId = null;
      this.username = null;
      this.accessToken = null;
      this.refreshToken = null;
      this.email = null;
      this.userRole = null;
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('email');
      localStorage.removeItem('userRole');
    },
  },
});

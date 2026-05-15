import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    accessToken: null,
    username: null,
    email: null,
    userRole: null,
  }),
  getters: {
    getUserId: state => state.userId,
    getAccessToken: state => state.accessToken,
    getUsername: state => state.username,
    getEmail: state => state.email,
    getUserRole: state => state.userRole,
    isLoggedIn: state => !!state.accessToken,
  },
  actions: {
    setAuthInfo(userId, username, accessToken, email, userRole) {
      this.userId = userId;
      this.username = username;
      this.accessToken = accessToken;
      this.email = email;
      this.userRole = userRole;
      localStorage.setItem('userId', userId);
      localStorage.setItem('username', username);
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('email', email);
      localStorage.setItem('userRole', userRole);
    },
    loadAuthInfo() {
      this.userId = localStorage.getItem('userId');
      this.username = localStorage.getItem('username');
      this.accessToken = localStorage.getItem('accessToken');
      this.email = localStorage.getItem('email');
      this.userRole = localStorage.getItem('userRole');
    },
    clearAuthInfo() {
      this.userId = null;
      this.username = null;
      this.accessToken = null;
      this.email = null;
      this.userRole = null;
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('email');
      localStorage.removeItem('userRole');
    },
  },
});

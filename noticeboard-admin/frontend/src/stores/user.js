import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    token: null,
  }),
  getters: {
    getUserId: state => state.userId,
    getToken: state => state.token,
    isAuthenticated: state => !!state.token,
  },
  actions: {
    setAuthInfo(userId, token) {
      this.userId = userId;
      this.token = token;
    },
    clearAuthInfo() {
      this.userId = null;
      this.token = null;
    },
  },
});

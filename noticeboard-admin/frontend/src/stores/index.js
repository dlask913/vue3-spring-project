import { defineStore } from 'pinia';

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    token: null,
  }),
  getters: {
    getUserId: (state) => state.userId,
    getToken: (state) => state.token,
    isAuthenticated: (state) => !!state.token,
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

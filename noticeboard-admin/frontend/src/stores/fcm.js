import { defineStore } from 'pinia';

export const useFcmStore = defineStore('fcm', {
  state: () => ({
    token: null,
  }),
   getters: {
    getToken: state => state.token
  },
  actions: {
    setToken(token) {
      this.token = token;
    },
  },
});

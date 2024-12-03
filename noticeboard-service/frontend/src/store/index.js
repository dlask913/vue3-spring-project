import { defineStore } from 'pinia'
import { useCookies } from 'vue3-cookies'

export const useToastStore = defineStore('toast', {
  state: () => ({
    showToast: false,
    toastMessage: '',
    toastAlertType: '',
  }),
  actions: {
    setToast(message, type = 'success') {
      this.toastMessage = message
      this.toastAlertType = type
      this.showToast = true

      // 토스트 메시지를 3초 후에 자동으로 숨김
      setTimeout(() => {
        this.clearToast()
      }, 3000)
    },
    clearToast() {
      this.showToast = false
      this.toastMessage = ''
      this.toastAlertType = ''
    },
  },
})

export const useStorageStore = defineStore('storage', {
  state: () => {
    const { cookies } = useCookies()
    const userIdInCookie = cookies.get('userId')
    const tokenInCookie = cookies.get('token')
    return {
      userId: userIdInCookie || '',
      token: tokenInCookie || '',
    }
  },
  getters: {
    isLogin(state) {
      return !!state.token
    },
    getToken(state) {
      return `Bearer ${state.token}`
    },
    getUserId(state) {
      return state.userId
    },
  },
  actions: {
    login(userId, token) {
      this.userId = userId
      this.token = token
    },
    logout() {
      const { cookies } = useCookies()
      this.userId = ''
      this.token = ''
      cookies.remove('userId')
      cookies.remove('token')
    },
  },
})

import { defineStore } from 'pinia';

export const useToastStore = defineStore('toast', {
    state: () => ({
        showToast: false,
        toastMessage: '',
        toastAlertType: ''
    }),
    actions: {
        setToast(message, type = 'success') {
            this.toastMessage = message;
            this.toastAlertType = type;
            this.showToast = true;

            // 토스트 메시지를 3초 후에 자동으로 숨김
            setTimeout(() => {
                this.clearToast();
            }, 3000);
        },
        clearToast() {
            this.showToast = false;
            this.toastMessage = '';
            this.toastAlertType = '';
        }
    }
});

export const useStorageStore = defineStore('token', {
    state: () => ({
        jwt: ''
    }),
    getters: {
        getToken(state) {
            return state.jwt;
        },
    },
    actions: {
        setToken(jwt) {
            this.jwt = jwt;
        },
        clearToast() {
            this.jwt = '';
        }
    }
});
import { defineStore } from 'pinia';
import { useCookies } from 'vue3-cookies';

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

export const useStorageStore = defineStore('storage', {
    state: () => {
        const {cookies} = useCookies();
        return {
            userId: '',
            token: '' || cookies.get('token'),
        };
    },
    getters: {
        getToken(state){
            return state.token;
        },
    },
    actions: {
        login(userId, token) {
            this.userId = userId;
            this.token = token;
        },
        logout() {
            const {cookies} = useCookies();
            this.userId = '';
            this.token = '';
            cookies.remove('token');
        }
    }
});
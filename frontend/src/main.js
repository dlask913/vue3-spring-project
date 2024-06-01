import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia';
import VueCookies from 'vue3-cookies'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';
import 'bootstrap-icons/font/bootstrap-icons.css';

createApp(App).use(router).use(createPinia()).
use(VueCookies, {
    expireTimes: "1h",
    secure: false
}).mount('#app')

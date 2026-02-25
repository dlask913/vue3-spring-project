import { initializeApp } from 'https://www.gstatic.com/firebasejs/12.9.0/firebase-app.js';
import {
  getMessaging,
  onBackgroundMessage,
} from 'https://www.gstatic.com/firebasejs/12.9.0/firebase-messaging-sw.js';

const firebaseConfig = {
  apiKey: 'AIzaSyBis3DdEsr1ld5v5WPlHNAEbqrToa6Uqv0',
  authDomain: 'toyproject-limnj.firebaseapp.com',
  projectId: 'toyproject-limnj',
  storageBucket: 'toyproject-limnj.firebasestorage.app',
  messagingSenderId: '1009234756382',
  appId: '1:1009234756382:web:add0045227a45e7356ff0f',
  measurementId: 'G-00E4XE048J',
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

onBackgroundMessage(messaging, payload => {
  self.registration.showNotification(payload.notification.title, {
    body: payload.notification.body,
    icon: '/icons/icon-128x128.png',
  });
});

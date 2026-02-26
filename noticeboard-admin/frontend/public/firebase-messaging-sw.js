// eslint-disable-next-line no-undef
importScripts(
  'https://www.gstatic.com/firebasejs/12.9.0/firebase-app-compat.js',
);
// eslint-disable-next-line no-undef
importScripts(
  'https://www.gstatic.com/firebasejs/12.9.0/firebase-messaging-compat.js',
);

// eslint-disable-next-line no-undef
firebase.initializeApp({
  apiKey: 'AIzaSyBis3DdEsr1ld5v5WPlHNAEbqrToa6Uqv0',
  authDomain: 'toyproject-limnj.firebaseapp.com',
  projectId: 'toyproject-limnj',
  storageBucket: 'toyproject-limnj.firebasestorage.app',
  messagingSenderId: '1009234756382',
  appId: '1:1009234756382:web:add0045227a45e7356ff0f',
  measurementId: 'G-00E4XE048J',
});

// eslint-disable-next-line no-undef
const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
  self.registration.showNotification(payload.notification.title, {
    body: payload.notification.body,
  });
});

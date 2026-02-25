// Import the functions you need from the SDKs you need
import { initializeApp } from 'firebase/app';
import { getMessaging } from 'firebase/messaging';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: 'AIzaSyBis3DdEsr1ld5v5WPlHNAEbqrToa6Uqv0',
  authDomain: 'toyproject-limnj.firebaseapp.com',
  projectId: 'toyproject-limnj',
  storageBucket: 'toyproject-limnj.firebasestorage.app',
  messagingSenderId: '1009234756382',
  appId: '1:1009234756382:web:add0045227a45e7356ff0f',
  measurementId: 'G-00E4XE048J',
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const messaging = getMessaging(app);

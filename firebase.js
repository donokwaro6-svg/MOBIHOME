// firebase.js _ for MobiHome Service
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyCSFU5lmI1qbnFCsH_5kaODD7tbaNi6_vs",
  authDomain: "mobihome-service.firebaseapp.com",
  projectId: "mobihome-service",
  storageBucket: "mobihome-service.firebasestorage.app",
  messagingSenderId: "517694308527",
  appId: "1:517694308527:web:28e8bcd8d19b8d64d4545a"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);

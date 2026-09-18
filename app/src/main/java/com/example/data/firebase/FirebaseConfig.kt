package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Centralized Firebase configuration and initialization matching firebase.js for MobiHome Service.
 *
 * Config:
 * - apiKey: "AIzaSyCSFU5lmI1qbnFCsH_5kaODD7tbaNi6_vs"
 * - authDomain: "mobihome-service.firebaseapp.com"
 * - projectId: "mobihome-service"
 * - storageBucket: "mobihome-service.firebasestorage.app"
 * - messagingSenderId: "517694308527"
 * - appId: "1:517694308527:web:28e8bcd8d19b8d64d4545a"
 */
object FirebaseConfig {
    private const val TAG = "FirebaseConfig"

    const val API_KEY = "AIzaSyCSFU5lmI1qbnFCsH_5kaODD7tbaNi6_vs"
    const val AUTH_DOMAIN = "mobihome-service.firebaseapp.com"
    const val PROJECT_ID = "mobihome-service"
    const val STORAGE_BUCKET = "mobihome-service.firebasestorage.app"
    const val MESSAGING_SENDER_ID = "517694308527"
    const val APP_ID = "1:517694308527:web:28e8bcd8d19b8d64d4545a"

    fun ensureInitialized(context: Context): FirebaseApp? {
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                apps.first()
            } else {
                val options = FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APP_ID)
                    .setProjectId(PROJECT_ID)
                    .setStorageBucket(STORAGE_BUCKET)
                    .setGcmSenderId(MESSAGING_SENDER_ID)
                    .build()
                FirebaseApp.initializeApp(context.applicationContext, options)
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp initialization note: ${e.message}")
            runCatching { FirebaseApp.getInstance() }.getOrNull()
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        ensureInitialized(context)
        return runCatching { FirebaseAuth.getInstance() }.getOrNull()
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        ensureInitialized(context)
        return runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    }
}

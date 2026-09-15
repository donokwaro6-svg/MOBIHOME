package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MobiHomeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ensureFirebaseInitialized()
    }

    private fun ensureFirebaseInitialized() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:1084282436848:android:mobihomeapp")
                    .setApiKey("AIzaSyMockKeyForOfflineResilienceMobihome")
                    .setProjectId("mobihome-app")
                    .setStorageBucket("mobihome-app.firebasestorage.app")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("MobiHomeApp", "Firebase initialized safely with fallback configuration.")
            }
        } catch (e: Exception) {
            Log.w("MobiHomeApp", "FirebaseApp init fallback caught: ${e.message}")
        }
    }
}

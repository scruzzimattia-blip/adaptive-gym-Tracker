package com.mattia.adaptivegymtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GymTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here
    }
}

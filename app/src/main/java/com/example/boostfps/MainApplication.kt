package com.example.boostfps

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi auto-copy crash log
        CrashHandler.init(this)
    }
}

package com.bedesv.frugaltracker

import android.app.Application
import android.content.Context

class FrugalTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContextManager.initialize(this)
    }

    object AppContextManager {
        private lateinit var appContext: Context

        fun initialize(application: Application) {
            appContext = application.applicationContext
        }

        fun getAppContext(): Context {
            return appContext
        }
    }
}
package com.bedesv.budgettracker

import android.app.Application
import android.content.Context

class BudgetTrackerApplication : Application() {
    init {
        val instance = this
    }

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
package com.miiiin15.whereru

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhereRuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var instance: WhereRuApplication
            private set
    }
}
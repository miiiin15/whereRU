package com.miiiin15.whereru

import android.app.Application
import androidx.annotation.CallSuper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhereRuApplication : Application() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: WhereRuApplication
            private set
    }
}
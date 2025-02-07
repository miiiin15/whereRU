package com.miiiin15.whereru.local.pref

import android.app.Application
import com.miiiin15.whereru.local.model.AuthInfoModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil @Inject constructor(application: Application) {

    private val sharedPreference = SharedPreference(application)

    var authInfoModel : AuthInfoModel?
        get() = sharedPreference.get(PREF_AUTH_INFO)
        set(value) {
            if (value != null) {
                sharedPreference.put(PREF_AUTH_INFO, value)
            }
        }

    fun clearAuthInfoModel(){
        sharedPreference.remove(PREF_AUTH_INFO)
    }


    companion object {
        private const val PREF_AUTH_INFO = "PREF_AUTH_INFO"
    }
}

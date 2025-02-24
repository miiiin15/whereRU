package com.miiiin15.whereru.local.impl

import android.app.Application
import com.miiiin15.whereru.local.model.AuthInfoModel
import com.miiiin15.whereru.local.pref.PrefUtil
import com.miiiin15.whereru.local.pref.SharedPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtilImpl @Inject constructor(application: Application) : PrefUtil {

    private val sharedPreference = SharedPreference(application)

    override var authInfoModel: AuthInfoModel?
        get() = sharedPreference.get(PREF_AUTH_INFO)
        set(value) {
            if (value != null) {
                sharedPreference.put(PREF_AUTH_INFO, value)
            }
        }

    override fun clearAuthInfoModel() {
        sharedPreference.remove(PREF_AUTH_INFO)
    }

    companion object {
        private const val PREF_AUTH_INFO = "PREF_AUTH_INFO"
    }
}
package com.miiiin15.whereru.local.pref

import com.miiiin15.whereru.local.model.AuthInfoModel

interface PrefUtil {
    var authInfoModel: AuthInfoModel?
    fun clearAuthInfoModel()
}
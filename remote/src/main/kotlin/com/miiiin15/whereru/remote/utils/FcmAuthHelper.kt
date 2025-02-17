package com.miiiin15.whereru.remote.utils

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.miiiin15.whereru.remote.Constant

object FcmAuthHelper {

    fun getAccessTokenFromServiceAccount(context: Context): String {
        val credentials = GoogleCredentials
            .fromStream(context.assets.open("fcm-service-account.json"))
            .createScoped(listOf(Constant.OAUTH_SCOPE))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }

}
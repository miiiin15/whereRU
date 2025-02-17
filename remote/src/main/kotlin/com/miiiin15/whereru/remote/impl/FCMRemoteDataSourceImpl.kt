package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.remote.FCMRemoteDataSource
import com.miiiin15.whereru.remote.model.FCMMessage
import com.miiiin15.whereru.remote.model.FCMRequestBody
import com.miiiin15.whereru.remote.service.FCMApiService
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class FCMRemoteDataSourceImpl  @Inject constructor(
    private val firebaseService: FirebaseService,
    private val fcmApiService: FCMApiService,
) : FCMRemoteDataSource {

    override suspend fun getFCMToken(): String =
        firebaseService.getFCMToken()

    override suspend fun sendPushMessage(
        token: String,
        message: Map<String, String>,
    ): Boolean {

        val fcmRequestBody = FCMRequestBody(
            message = FCMMessage(
                token = token,
                notification = null,
                data = message
            )
        )

        val response = fcmApiService.sendPushMessage(fcmRequestBody)

        return response.isSuccessful
    }
}
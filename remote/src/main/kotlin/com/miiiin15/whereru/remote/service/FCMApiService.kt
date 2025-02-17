package com.miiiin15.whereru.remote.service

import com.miiiin15.whereru.remote.BuildConfig
import com.miiiin15.whereru.remote.model.FCMRequestBody
import com.miiiin15.whereru.remote.model.FCMResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApiService {

    @POST("v1/projects/${BuildConfig.FCM_PROJECT_ID}/messages:send")
    suspend fun sendPushMessage(
        @Body body: FCMRequestBody
    ): Response<FCMResponse>
}
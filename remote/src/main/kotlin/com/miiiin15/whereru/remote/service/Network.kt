package com.miiiin15.whereru.remote.service

import android.content.Context
import com.miiiin15.whereru.common.extension.gson
import com.miiiin15.whereru.remote.utils.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIME_OUT = 20

fun createFCMApiService(context: Context, baseUrl: String): FCMApiService {
    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        addInterceptor(AuthInterceptor(context))
    }.build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(FCMApiService::class.java)
}
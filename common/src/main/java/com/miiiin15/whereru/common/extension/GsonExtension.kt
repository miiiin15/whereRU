package com.miiiin15.whereru.common.extension

import android.util.Log
import com.google.gson.GsonBuilder

val gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .create()

fun Any.toJson(): String? = gson.toJson(this)

inline fun <reified T> String.fromJson(): T? =
    runCatching {
        gson.fromJson(this, T::class.java)
    }.onFailure {
        Log.e("GsonExtension", "[${T::class.java.name}] json 파싱 오류 발생 - ${it.message} : $this")
    }.getOrNull()

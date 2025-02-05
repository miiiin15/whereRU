package com.miiiin15.whereru.data.local

interface AuthLocalDataSource {

    suspend fun login(email: String, password: String): Boolean

    suspend fun register(email: String, password: String, nickname: String?): Boolean

}
package com.miiiin15.whereru.data.remote

interface AuthRemoteDataSource {

    suspend fun login(email: String, password: String): String

    suspend fun register(email: String, password: String): String

}
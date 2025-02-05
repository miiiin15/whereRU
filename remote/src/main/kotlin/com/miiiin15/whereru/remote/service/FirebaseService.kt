package com.miiiin15.whereru.remote.service



interface FirebaseService {

    suspend fun login(email: String, password: String): String

    suspend fun register(email: String, password: String): String
}
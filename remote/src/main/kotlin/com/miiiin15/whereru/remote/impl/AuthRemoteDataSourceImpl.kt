package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.remote.AuthRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): String =
        firebaseService.login(email, password)

    override suspend fun register(email: String, password: String): String =
        firebaseService.register(email, password)
}

package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.remote.AuthRemoteDataSource
import com.miiiin15.whereru.data.remote.ProfileRemoteDataSource
import com.miiiin15.whereru.remote.model.GetProfileResponse
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : ProfileRemoteDataSource {

    override suspend fun setProfile(
        profile: ProfileEntity
    ) = firebaseService.setProfile(profile)

    override suspend fun getProfile(userId: String): ProfileEntity =
        firebaseService.getProfile(userId)

}

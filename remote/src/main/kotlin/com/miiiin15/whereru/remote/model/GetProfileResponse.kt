package com.miiiin15.whereru.remote.model

import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.remote.utils.RemoteMapper

data class GetProfileResponse(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val lastLoginAt: Long
) : RemoteMapper<ProfileEntity> {

    override fun toData(): ProfileEntity {
        return ProfileEntity(
            userId,
            nickname,
            profileImageUrl,
            lastLoginAt
        )
    }
}
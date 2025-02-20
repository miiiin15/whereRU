package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.LiveLocationUser

data class LiveLocationUserEntity(
    val userId: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    val location: LiveLocationDataEntity = LiveLocationDataEntity()
) : DataMapper<LiveLocationUser> {
    override fun toDomain(): LiveLocationUser =
        LiveLocationUser(userId, nickname, profileImageUrl, location.toDomain())
}
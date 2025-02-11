package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.LiveLocationUser

data class LiveLocationUserEntity(
    val userId: String = "",
    val nickname: String = "",
    val location: LiveLocationDataEntity = LiveLocationDataEntity()
) : DataMapper<LiveLocationUser> {
    override fun toDomain(): LiveLocationUser =
        LiveLocationUser(userId, nickname, location.toDomain())
}
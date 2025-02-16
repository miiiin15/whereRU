package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.User

// snapshot 역직렬화(Deserialization)를 위해 기본값 포함
data class ProfileEntity(
    val userId: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = "",
    val sessionId: String? = "",
    val lastLoginAt: Long = 0,
    val fcmToken: String? = ""
) : DataMapper<User> {
    override fun toDomain(): User {
        return User(
            userId = userId,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            sessionId = sessionId,
            lastLoginAt = lastLoginAt,
            fcmToken = fcmToken
        )
    }
}

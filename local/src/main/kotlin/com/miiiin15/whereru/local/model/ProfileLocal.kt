package com.miiiin15.whereru.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.local.LocalMapper
import com.miiiin15.whereru.local.room.RoomConstant

@Entity(tableName = RoomConstant.Table.USERS)
data class ProfileLocal(
    @PrimaryKey
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val sessionId: String?,
    val lastLoginAt: Long = 0,
    val fcmToken: String?
) : LocalMapper<ProfileEntity> {
    override fun toData(): ProfileEntity =
        ProfileEntity(userId, nickname, profileImageUrl, sessionId, lastLoginAt, fcmToken)
}

fun ProfileEntity.toLocal(): ProfileLocal =
    ProfileLocal(userId, nickname, profileImageUrl, sessionId, lastLoginAt, fcmToken)

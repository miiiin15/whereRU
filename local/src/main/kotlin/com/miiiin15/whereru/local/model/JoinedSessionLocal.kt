package com.miiiin15.whereru.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miiiin15.whereru.data.model.JoinedSessionEntity
import com.miiiin15.whereru.local.LocalMapper
import com.miiiin15.whereru.local.room.RoomConstant

@Entity(tableName = RoomConstant.Table.JOINED_SESSIONS)
data class JoinedSessionLocal(
    @PrimaryKey
    val sessionId : String,
    val hostNickname: String,
    val participationTime: Long
) : LocalMapper<JoinedSessionEntity>{
    override fun toData(): JoinedSessionEntity =
        JoinedSessionEntity(sessionId, hostNickname, participationTime)
}

fun JoinedSessionEntity.toLocal(): JoinedSessionLocal =
    JoinedSessionLocal(sessionId, hostNickname, participationTime)
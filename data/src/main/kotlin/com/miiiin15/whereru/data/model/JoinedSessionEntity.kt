package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.JoinedSession

data class JoinedSessionEntity(
    val sessionId : String = "",
    val hostNickname: String = "",
    val participationTime: Long = 0L,
) : DataMapper<JoinedSession> {
    override fun toDomain(): JoinedSession =
        JoinedSession(
            sessionId,
            hostNickname,
            participationTime
        )
}
package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.LiveLocationSession

data class LiveLocationEntity(
    val hostUid: String = "",
    val isActive: Boolean = false,
    val startedAt: Long = 0L,
    val users: Map<String, LiveLocationUserEntity> = emptyMap()
) : DataMapper<LiveLocationSession> {
    override fun toDomain(): LiveLocationSession =
        LiveLocationSession(
            hostUid,
            isActive,
            startedAt,
            users.mapValues { it.value.toDomain() }
        )
}
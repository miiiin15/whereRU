package com.miiiin15.whereru.remote.model

import com.google.firebase.database.PropertyName
import com.miiiin15.whereru.data.model.LiveLocationEntity
import com.miiiin15.whereru.remote.utils.RemoteMapper

/**
 * 실시간 위치 공유 세션 응답: 최상위 클래스
 */
data class LiveLocationResponse(
    @get:PropertyName("host_uid") @set:PropertyName("host_uid")
    var hostUid: String = "",

    @get:PropertyName("is_active") @set:PropertyName("is_active")
    var isActive: Boolean = false,

    @get:PropertyName("started_at") @set:PropertyName("started_at")
    var startedAt: Long = 0L,

    var users: Map<String, LiveLocationUserBlock> = emptyMap()
) : RemoteMapper<LiveLocationEntity> {
    override fun toData(): LiveLocationEntity =
        LiveLocationEntity(
            hostUid,
            isActive,
            startedAt,
            users.mapValues { it.value.toData() })
}
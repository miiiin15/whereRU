package com.miiiin15.whereru.remote.model

import com.google.firebase.database.PropertyName
import com.miiiin15.whereru.data.model.MyLocationEntity

data class CreateSessionRequest(

    @get:PropertyName("host_uid")
    @set:PropertyName("host_uid")
    var hostId: String = "",

    @get:PropertyName("started_at")
    @set:PropertyName("started_at")
    var startedAt: Long = 0L,

    @get:PropertyName("is_active")
    @set:PropertyName("is_active")
    var isActive: Boolean = true,

    var users: Map<String, MyLocationEntity> = emptyMap()
)


package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.LiveLocationData

data class LiveLocationDataEntity(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
) : DataMapper<LiveLocationData> {
    override fun toDomain(): LiveLocationData =
        LiveLocationData(latitude, longitude, timestamp)
}
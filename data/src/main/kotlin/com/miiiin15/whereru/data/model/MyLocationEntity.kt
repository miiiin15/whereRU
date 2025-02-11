package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.MyLocationData

data class MyLocationEntity(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) : DataMapper<MyLocationData> {
    override fun toDomain(): MyLocationData {
        return MyLocationData(
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp
        )
    }
}

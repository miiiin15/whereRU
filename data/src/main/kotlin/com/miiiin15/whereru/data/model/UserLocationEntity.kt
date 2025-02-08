package com.miiiin15.whereru.data.model

import com.miiiin15.whereru.data.DataMapper
import com.miiiin15.whereru.domain.model.LocationData

data class UserLocationEntity(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) : DataMapper<LocationData> {
    override fun toDomain(): LocationData {
        return LocationData(
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp
        )
    }
}

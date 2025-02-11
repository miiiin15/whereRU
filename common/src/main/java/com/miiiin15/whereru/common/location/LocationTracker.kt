package com.miiiin15.whereru.common.location

import com.miiiin15.whereru.common.location.model.RawLocation

interface LocationTracker {
    fun startTracking(onLocationUpdated: (RawLocation) -> Unit)
    fun stopTracking()
    suspend fun getCurrentLocation(): RawLocation?
}
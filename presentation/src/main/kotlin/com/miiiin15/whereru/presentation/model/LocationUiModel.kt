package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.location.model.RawLocation
import com.miiiin15.whereru.domain.model.LocationData

data class LocationUiModel(
    val latitude: Double,  // 위도
    val longitude: Double, // 경도
    val timestamp: Long    // 위치 업데이트 시간
)

fun LocationData.toPresentation() = LocationUiModel(latitude, longitude, timestamp)
fun RawLocation.toPresentation() = LocationUiModel(latitude, longitude, timestamp)
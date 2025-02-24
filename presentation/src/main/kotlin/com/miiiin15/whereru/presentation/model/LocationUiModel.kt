package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.location.model.RawLocation
import com.miiiin15.whereru.domain.model.LiveLocationData
import com.miiiin15.whereru.domain.model.MyLocationData

data class LocationUiModel(
    val latitude: Double,  // 위도
    val longitude: Double, // 경도
    val timestamp: String    // 위치 업데이트 시간
)

fun MyLocationData.toPresentation(): LocationUiModel {
    val timeValue: String = timestamp.toString()
    return LocationUiModel(latitude, longitude, timeValue)
}

fun LiveLocationData.toPresentation(): LocationUiModel {
    val timeValue: String = timestamp.toString()
    return LocationUiModel(latitude, longitude, timeValue)
}

fun RawLocation.toPresentation(): LocationUiModel {
    val timeValue: String = timestamp.toString()
    return LocationUiModel(latitude, longitude, timeValue)
}
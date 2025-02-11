package com.miiiin15.whereru.remote.model

import com.miiiin15.whereru.data.model.LiveLocationDataEntity
import com.miiiin15.whereru.remote.utils.RemoteMapper

/**
 * 실시간 위치 공유 세션 응답: 말단 클래스 -
 * 유저 위치 좌표값 블록
 */
data class LiveLocationDataBlock(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
) : RemoteMapper<LiveLocationDataEntity>{
    override fun toData(): LiveLocationDataEntity =
        LiveLocationDataEntity(latitude, longitude, timestamp)
}

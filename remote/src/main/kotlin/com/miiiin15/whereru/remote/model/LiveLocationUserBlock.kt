package com.miiiin15.whereru.remote.model

import com.miiiin15.whereru.data.model.LiveLocationUserEntity
import com.miiiin15.whereru.remote.utils.RemoteMapper

/**
 * 실시간 위치 공유 세션 응답: 중간 클래스 -
 * 위치 정보를 포함한 유저 정보 블록
 */
data class LiveLocationUserBlock(
    val userId: String = "",
    val nickname: String = "",
    val location: LiveLocationDataBlock = LiveLocationDataBlock()
) : RemoteMapper<LiveLocationUserEntity> {
    override fun toData(): LiveLocationUserEntity =
        LiveLocationUserEntity(userId, nickname, location.toData())
}
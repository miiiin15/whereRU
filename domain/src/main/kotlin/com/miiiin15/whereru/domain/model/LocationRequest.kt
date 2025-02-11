package com.miiiin15.whereru.domain.model

data class LocationRequest(
    val requestId: String,   // 요청 고유 ID
    val senderId: String,    // 요청을 보낸 사용자 ID
    val receiverId: String,  // 요청을 받은 사용자 ID
    val myLocationData: MyLocationData,  // 요청자의 위치 정보
    val status: RequestStatus // 요청 상태
)

enum class RequestStatus {
    PENDING, ACCEPTED, REJECTED
}

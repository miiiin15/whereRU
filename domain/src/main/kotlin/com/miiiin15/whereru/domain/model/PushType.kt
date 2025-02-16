package com.miiiin15.whereru.domain.model

/**
 * FCM 메시지 타입 정의
 */
enum class PushType {
    REQUEST_LOCATION,
    RESPONSE_LOCATION,
    CANCEL_SESSION;

    companion object {
        fun from(value: String?): PushType? =
            values().firstOrNull { it.name == value }
    }
}
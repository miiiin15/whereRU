package com.miiiin15.whereru.domain.model

/**
 * 위치 요청에 대한 응답 타입 (수락/거절)
 */
enum class ResponseType {
    ACCEPT,
    DECLINE;

    companion object {
        fun from(value: String?): ResponseType? =
            entries.firstOrNull { it.name == value }
    }
}
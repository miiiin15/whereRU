package com.miiiin15.whereru.common.utils


import java.util.UUID

object UUIDUtil {

    /**
     * 랜덤 UUID 문자열을 생성하고, 하이픈(-)은 제거한 32자리 문자열을 반환함.
     * 예: "e4f54f57ffb147a4a78e8a0b6dcbb85d"
     */
    fun generateSessionId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    /**
     * 짧은 버전의 세션 ID를 원할 경우 사용 (앞 8자리만 사용)
     * 예: "e4f54f57"
     */
    fun generateShortSessionId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}
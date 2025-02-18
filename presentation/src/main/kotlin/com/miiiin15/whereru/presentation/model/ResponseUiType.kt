package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.ResponseType

/**
 * 위치 요청에 대한 응답 타입 (수락/거절)
 */
enum class ResponseUiType {
    ACCEPT,
    DECLINE;
}

fun ResponseType.toPresentation(): ResponseUiType? {
    return when (this) {
        ResponseType.ACCEPT -> ResponseUiType.ACCEPT
        ResponseType.DECLINE -> ResponseUiType.DECLINE
        else -> null
    }
}
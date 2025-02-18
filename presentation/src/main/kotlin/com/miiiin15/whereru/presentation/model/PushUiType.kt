package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.PushType

/**
 * FCM 메시지 타입 정의
 */
enum class PushUiType {
    REQUEST_LOCATION,
    RESPONSE_LOCATION,
    CANCEL_SESSION;

}

fun PushType.toPresentation(): PushUiType {
    return when (this) {
        PushType.REQUEST_LOCATION -> PushUiType.REQUEST_LOCATION
        PushType.RESPONSE_LOCATION -> PushUiType.RESPONSE_LOCATION
        PushType.CANCEL_SESSION -> PushUiType.CANCEL_SESSION
    }
}


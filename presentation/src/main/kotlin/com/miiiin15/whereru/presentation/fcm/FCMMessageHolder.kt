package com.miiiin15.whereru.presentation.fcm

import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.presentation.model.PushMessageUiModel
import com.miiiin15.whereru.presentation.model.toPresentation

/**
 * FCM 수신 시 전달받은 PushMessage를 앱 진입 이후까지 보존하기 위한 전역 저장소.
 * 로그인/인증 후 후속 처리에 활용됨.
 */
object FCMMessageHolder {

    private var message: PushMessageUiModel? = null

    /** 메시지를 저장함 (앱 시작 시점에서 저장) */
    fun set(message: PushMessage) {
        this.message = message.toPresentation()
    }

    /** 메시지를 저장함 (메인 엑티비티 시점에서 저장) */
    fun set(message: PushMessageUiModel) {
        this.message = message
    }

    /** 저장된 메시지를 반환하고, 내부 상태는 초기화함 */
    fun consume(): PushMessageUiModel? {
        val result = message
        message = null
        return result
    }

    /** 현재 메시지를 그대로 반환 (소비하지 않음) */
    fun peek(): PushMessageUiModel? {
        return message
    }

    /** 저장 여부 확인용 */
    fun hasPendingMessage(): Boolean {
        return message != null
    }
}
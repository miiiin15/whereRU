package com.miiiin15.whereru.ui.home

import android.app.NotificationManager
import android.content.Context
import com.miiiin15.whereru.presentation.model.PushMessageUiModel
import com.miiiin15.whereru.presentation.model.PushUiType
import com.miiiin15.whereru.presentation.model.ResponseUiType

object FcmActionHandler {

    fun handleFcmAction(
        context: Context,
        message: PushMessageUiModel,
        showCustomBottomSheet: (
            title: String,
            leftButtonText: String,
            rightButtonText: String,
            onLeftButtonClick: () -> Unit,
            onRightButtonClick: () -> Unit
        ) -> Unit,
        showCustomAlert: (message: String, onConfirm: (() -> Unit)?) -> Unit,
        sendResponsePushMessage: (PushMessageUiModel, Boolean) -> Unit,
        participationSession: (String, String) -> Unit
    ) {
        // 포그라운드에서 수신한 PushMessage 삭제
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(message.notificationId)

        when (message.type) {
            PushUiType.REQUEST_LOCATION -> {
                showCustomBottomSheet(
                    "${message.fromNickname}님이 ${message.timestamp} 위치 공유 요청을 보냈습니다.\n수락하시겠습니까?",
                    "거절",
                    "수락",
                    { sendResponsePushMessage(message, false) },
                    { sendResponsePushMessage(message, true) }
                )
            }

            PushUiType.RESPONSE_LOCATION -> {
                message.response?.let {
                    when (it) {
                        ResponseUiType.ACCEPT -> {
                            showCustomAlert("${message.fromNickname}님이 위치 공유를 수락했습니다.\n 참여 하시겠습니까?") {
                                participationSession(message.sessionId, message.fromNickname)
                            }
                        }

                        ResponseUiType.DECLINE -> {
                            showCustomAlert("${message.fromNickname}님이 위치 공유를 거절했습니다.") {}
                        }
                    }
                } ?: run {
                    showCustomAlert("응답을 받을 수 없습니다.") {}
                }
            }

            PushUiType.CANCEL_SESSION -> {
                // 세션 종료 처리 로직
            }
        }
    }
}
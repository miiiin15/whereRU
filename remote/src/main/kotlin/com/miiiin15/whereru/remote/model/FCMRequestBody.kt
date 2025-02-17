package com.miiiin15.whereru.remote.model

data class FCMRequestBody(
    val message: FCMMessage
)

data class FCMMessage(
    val token: String,
    val notification: NotificationPayload? = null,
    val data: Map<String, String>? = null
)

data class NotificationPayload(
    val title: String,
    val body: String
)
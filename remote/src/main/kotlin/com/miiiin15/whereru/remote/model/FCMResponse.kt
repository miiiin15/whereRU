package com.miiiin15.whereru.remote.model

import com.google.gson.annotations.SerializedName

data class FCMResponse(
    @SerializedName("multicast_id") val multicastId: Long,
    @SerializedName("success") val success: Int,
    @SerializedName("failure") val failure: Int,
    @SerializedName("canonical_ids") val canonicalIds: Int,
    @SerializedName("results") val results: List<FCMResult>
)

data class FCMResult(
    @SerializedName("message_id") val messageId: String? = null,
    @SerializedName("error") val error: String? = null
)
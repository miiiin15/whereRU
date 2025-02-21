package com.miiiin15.whereru.ui.location

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.miiiin15.whereru.presentation.model.LiveLocationUserUiModel
import com.miiiin15.whereru.presentation.model.LocationUiModel
import com.miiiin15.whereru.ui.R

class MarkerManager(
    private val mMap: GoogleMap?,
    private val resources: Resources
) {
    private val markers = mutableMapOf<String, Marker>()

    var onUserStateChanged: ((removedUser: LiveLocationUserUiModel?, newUser: LiveLocationUserUiModel?) -> Unit)? =
        null

    private var _removedUser: LiveLocationUserUiModel? = null
    private var _newUser: LiveLocationUserUiModel? = null

    fun getMarkerPosition(userId: String?): LatLng? {
        if (userId == null) return null
        return markers[userId]?.position
    }


    fun updateMarkers(users: Map<String, LiveLocationUserUiModel>) {
        val newMarkers = mutableMapOf<String, Marker>()

        users.forEach { (userId, user) ->
            val newPosition = LatLng(user.location.latitude, user.location.longitude)
            val newTime = user.location.timestamp

            if (!markers.containsKey(userId)) {  // uid 가 다르면 새로 추가
                _newUser = user

                val bitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.profile_image_default)
                        .copy(Bitmap.Config.ARGB_8888, true)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                val canvas = Canvas(scaledBitmap)

                if (!user.profileImageUrl.isNullOrBlank()) {
                    val paint = Paint().apply {
                        colorFilter = android.graphics.PorterDuffColorFilter(
                            user.profileImageUrl!!.toInt(),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                    canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
                }

                val marker = mMap?.addMarker(
                    MarkerOptions()
                        .position(newPosition)
                        .title(user.nickname)
                        .snippet(newTime)
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                )

                if (marker != null) {
                    marker.tag = user.userId
                    newMarkers[userId] = marker
                }
            } else { //  uid 가 동일하면 업데이트
                val marker = markers[userId]
                if (marker?.position != newPosition) {
                    animateMarkerTo(marker, newPosition)
                }
                marker?.snippet = newTime
                newMarkers[userId] = marker!!
            }
        }

        // 기존 마커 목록중 사라진 마커 후처리
        markers.keys.subtract(newMarkers.keys).forEach { userId ->
            _removedUser =
                LiveLocationUserUiModel(
                    userId = userId,
                    nickname = markers[userId]?.title ?: "",
                    profileImageUrl = null,
                    location = LocationUiModel(0.0, 0.0, "")
                )
            markers[userId]?.remove()
        }

        // 기존 마커 목록을 새로운 마커 목록으로 대체
        markers.clear()
        markers.putAll(newMarkers)

        // 상태 변화 콜백 호출
        onUserStateChanged?.invoke(_removedUser, _newUser)

        // 상태 초기화
        _removedUser = null
        _newUser = null
    }

    // 애니메이션을 사용하여 마커를 새로운 위치로 이동
    private fun animateMarkerTo(marker: Marker?, newPosition: LatLng) {
        val startPosition = marker?.position
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener { animation ->
            val v = animation.animatedFraction
            val lng = v * newPosition.longitude + (1 - v) * startPosition!!.longitude
            val lat = v * newPosition.latitude + (1 - v) * startPosition.latitude
            marker?.position = LatLng(lat, lng)
        }
        valueAnimator.start()
    }
}
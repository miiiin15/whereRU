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
import com.miiiin15.whereru.ui.R

class MarkerManager(
    private val mMap: GoogleMap?,
    private val resources: Resources
) {
    private val markers = mutableMapOf<String, Marker>()
    var removedUserNickname: String? = null
    var newUserNickname: String? = null

    fun getMarkerPosition(userId: String?): LatLng? {
        if (userId == null) return null
        return markers[userId]?.position
    }


    fun updateMarkers(users: Map<String, LiveLocationUserUiModel>) {
        val newMarkers = mutableMapOf<String, Marker>()

        users.forEach { (userId, user) ->
            val newPosition = LatLng(user.location.latitude, user.location.longitude)
            val newTime = user.location.timestamp
            if (markers.containsKey(userId)) { // 좌표가 동일한 기존 마커가 있는 경우
                val marker = markers[userId]
                if (marker?.position != newPosition) {
                    animateMarkerTo(marker, newPosition)
                }
                marker?.snippet = newTime
                newMarkers[userId] = marker!!
            } else { // 좌표가 다른 새로운 마커인 경우

                newUserNickname = user.nickname

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
            }
        }

        markers.keys.subtract(newMarkers.keys).forEach { userId ->
            removedUserNickname = markers[userId]?.title
            markers[userId]?.remove()
        }

        markers.clear()
        markers.putAll(newMarkers)
    }

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
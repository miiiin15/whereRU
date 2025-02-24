package com.miiiin15.whereru.ui.location

import android.annotation.SuppressLint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MapManager(
    private val googleMap: GoogleMap
) {

    init {
        moveCamera(LatLng(37.556, 126.97), 12f)
    }

    @SuppressLint("MissingPermission")
    fun initializeMap(
        onMarkerClick: (marker: Marker) -> Unit,
        onMapClick: () -> Unit,
        onInitialMyLocationCallback: (location: LatLng) -> Unit
    ) {

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.isMyLocationEnabled = true

        // GoogleMap UI 설정
        with(googleMap.uiSettings) {
            isZoomControlsEnabled = true
            isCompassEnabled = false
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
            isScrollGesturesEnabled = true
            isZoomGesturesEnabled = true
            isRotateGesturesEnabled = false
        }

        // Marker 클릭 리스너
        googleMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            onMarkerClick(marker)
            true
        }

        // Map 클릭 리스너
        googleMap.setOnMapClickListener {
            onMapClick()
        }

        // 내 위치 리스너 (1 회만 호출)
        googleMap.setOnMyLocationChangeListener { location ->
            onInitialMyLocationCallback(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
            googleMap.setOnMyLocationChangeListener(null) // 리스너 제거
        }
    }

    fun moveCamera(latLng: LatLng, zoomLevel: Float = googleMap.cameraPosition.zoom) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
        )
    }

}
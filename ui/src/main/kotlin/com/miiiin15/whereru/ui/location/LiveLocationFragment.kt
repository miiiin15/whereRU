package com.miiiin15.whereru.ui.location

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.miiiin15.whereru.presentation.model.LiveLocationUserUiModel
import com.miiiin15.whereru.presentation.viewmodel.LiveLocationViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentLiveLocationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LiveLocationFragment :
    BaseFragment<FragmentLiveLocationBinding, LiveLocationViewModel, LiveLocationViewModel.Event>(
        R.layout.fragment_live_location
    ), OnMapReadyCallback {
    override val viewModel: LiveLocationViewModel by viewModels()

    private var mMap: GoogleMap? = null
    private val markers = mutableMapOf<String, Marker>()
    private var isCameraMoved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel {
            users observe { users ->
                updateMarkers(users)
                binding.liveLocationMarkerCountText.text = "${users.size}"
            }
            myLocation observe {
                if (!isCameraMoved && myLocation.value != null) {
                    mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(myLocation.value!!.latitude, myLocation.value!!.longitude),
                            18f
                        )
                    )
                    isCameraMoved = true
                }
            }

            isWantReceive observe { isChecked ->
                val color = if (isChecked) "#90FFFFFF" else "#80AAAAAA"
                binding.liveLocationReceiveSwitch.setBackgroundColor(Color.parseColor(color))
            }

            isWantTransmit observe { isChecked ->
                val color = if (isChecked) "#90FFFFFF" else "#80AAAAAA"
                binding.liveLocationTransmitSwitch.setBackgroundColor(Color.parseColor(color))
            }
        }

        binding {
            vm = viewModel

            liveLocationTransmitSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setTrackingState(isChecked)
            }

            liveLocationReceiveSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setObserveSessionState(isChecked)
            }


        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setObserveSessionState(false)
        viewModel.setTrackingState(false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentMyLocation()
    }

    private fun updateMarkers(users: Map<String, LiveLocationUserUiModel>) {
        val newMarkers = mutableMapOf<String, Marker>()

        // 기존 마커와 새로운 유저 목록을 비교하여 갱신
        users.forEach { (userId, user) ->
            val newPosition = LatLng(user.location.latitude, user.location.longitude)
            if (markers.containsKey(userId)) {
                val marker = markers[userId]
                // 기존 마커 위치가 변경된 경우에만 위치 갱신
                if (marker?.position != newPosition) {
                    animateMarkerTo(marker, newPosition)
                }
                newMarkers[userId] = marker!!
            } else {
                // 새로운 마커 추가
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.profile_image_default)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false) // 원하는 크기로 조절
                val marker = mMap?.addMarker(
                    MarkerOptions()
                        .position(newPosition)
                        .title(user.nickname)
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                )
                if (marker != null) {
                    newMarkers[userId] = marker
                }
            }
        }

        // 기존 마커 중 새로운 목록에 없는 마커 제거
        markers.keys.subtract(newMarkers.keys).forEach { userId ->
            markers[userId]?.remove()
        }

        // 마커 목록 갱신
        markers.clear()
        markers.putAll(newMarkers)
    }

    private fun animateMarkerTo(marker: Marker?, newPosition: LatLng) {
        val startPosition = marker?.position
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000 // 애니메이션 지속 시간 (밀리초)
        valueAnimator.addUpdateListener { animation ->
            val v = animation.animatedFraction
            val lng = v * newPosition.longitude + (1 - v) * startPosition!!.longitude
            val lat = v * newPosition.latitude + (1 - v) * startPosition.latitude
            marker?.position = LatLng(lat, lng)
        }
        valueAnimator.start()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.isMyLocationEnabled = true

        with(mMap!!.uiSettings) {
            isZoomControlsEnabled = true
            isCompassEnabled = false
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
            isScrollGesturesEnabled = true
            isZoomGesturesEnabled = true
            isRotateGesturesEnabled = false
        }

        // 초기 카메라 위치 설정
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.556, 126.97), 10f))
    }

    override fun handleEvent(event: LiveLocationViewModel.Event) {
        when (event) {
            is LiveLocationViewModel.Event.UsersUpdated -> updateMarkers(event.users)
        }
    }
}
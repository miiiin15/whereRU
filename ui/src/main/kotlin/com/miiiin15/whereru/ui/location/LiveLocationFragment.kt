package com.miiiin15.whereru.ui.location

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var markerManager: MarkerManager
    private var isCameraMoved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.isHost.value ) {
                showCustomAlert("세션을 종료하시겠습니까?") {

                }
            } else
            showCustomAlert("세션을 종료하시겠습니까?") {
                viewModel.deleteMyLocation {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        markerManager = MarkerManager(mMap, resources)

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

        // ViewModel 관찰자 설정
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewModel {
            users observe { users ->
                markerManager.updateMarkers(users)

                if (markerManager.removedUserNickname != null) {
                    Toast.makeText(
                        requireContext(),
                        "${markerManager.removedUserNickname}님이 세션을 나가셨습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    markerManager.removedUserNickname = null
                }


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

    override fun handleEvent(event: LiveLocationViewModel.Event) {
        when (event) {
            is LiveLocationViewModel.Event.UsersUpdated -> markerManager.updateMarkers(event.users)
        }
    }
}
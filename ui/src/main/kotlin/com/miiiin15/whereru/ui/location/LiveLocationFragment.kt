package com.miiiin15.whereru.ui.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
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
    private var targetUerId: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showCustomAlert("세션을 이탈하시겠습니까?") {
                // TODO : 호스트 이탈 처리 필요시 viewModel.isHost 이용
                viewModel.deleteMyLocation()
                requireActivity().supportFragmentManager.popBackStack()
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


        // ViewModel 관찰자 설정
        setupViewModelObservers()

        // Map 관련 설정
        setupMapView()
    }

    private fun setupViewModelObservers() {
        viewModel {
            users observe { users ->
                markerManager.updateMarkers(users)
                binding.liveLocationMarkerCountText.text = "${users.size}"

                // 인 엔 아웃 처리
                if (markerManager.removedUserNickname != null) {
                    showToast("${markerManager.removedUserNickname}님이 세션을 나가셨습니다.")
                    markerManager.removedUserNickname = null
                }
                if (!markerManager.newUserNickname.isNullOrBlank()) {
                    showToast("${markerManager.newUserNickname}님이 세션에 들어오셨습니다.")
                    markerManager.newUserNickname = null
                }

                // 타켓 카메라 추적
                if (markerManager.getMarkerPosition(targetUerId) != null) {
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            markerManager.getMarkerPosition(targetUerId)!!,
                            18f
                        )
                    )
                } else {
                    binding.liveLocationChaseIcon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_off)
                    )
                    binding.liveLocationChaseNickname.text = "없음"
                    targetUerId = ""
                }

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

    private fun setupMapView() {
        mMap!!.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.556, 126.97), 10f))

            setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                binding.liveLocationChaseIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_on)
                )
                binding.liveLocationChaseNickname.text = marker.title
                targetUerId = marker.tag as String
                true
            }

            setOnMapClickListener {
                binding.liveLocationChaseIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_off)
                )
                binding.liveLocationChaseNickname.text = "없음"
                targetUerId = null
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
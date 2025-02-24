package com.miiiin15.whereru.ui.location

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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

    private var mapManager: MapManager? = null
    private lateinit var markerManager: MarkerManager

    private var isCameraMove = false
    private var targetUerId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showLoading("LiveLocationFragment")
        postponeEnterTransition()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showCustomAlert("세션을 이탈하시겠습니까?") { exitSession() }
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding {
            vm = viewModel

            liveLocationTransmitSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setTrackingState(isChecked)
            }

            liveLocationReceiveSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setObserveSessionState(isChecked)
            }

            liveLocationMarkerContainer.setOnClickListener {
                if (markerManager.getNextMarker() != null) {
                    mapManager?.moveCamera(
                        markerManager.getNextMarker()!!.position
                    )
                    targetOn(markerManager.getNextMarker()!!)
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        // 지도 초기화
        mapManager = MapManager(googleMap).apply {
            initializeMap(
                onMarkerClick = { targetOn(it) },
                onMapClick = { targetOff() },
                onInitialMyLocationCallback = { startTransition(it) }
            )
        }

        // 마커 매니저 초기화
        markerManager = MarkerManager(googleMap, resources).apply {
            onUserStateChanged = { removedUser, newUser ->
                removedUser?.let {
                    showToast("${it.nickname}님이 세션을 나가셨습니다.")
                }
                newUser?.let {
                    showToast("${it.nickname}님이 세션에 들어오셨습니다.")
                    startTransition(LatLng(it.location.latitude, it.location.longitude))
                }
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun targetOn(marker: Marker) {
        binding.liveLocationChaseIcon.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_on)
        )
        binding.liveLocationChaseNickname.text = marker.title
        targetUerId = marker.tag as String
    }

    private fun targetOff() {
        binding.liveLocationChaseIcon.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_off)
        )
        binding.liveLocationChaseNickname.text = "없음"
        targetUerId = null
    }

    private fun startTransition(location: LatLng?) {
        if (isCameraMove) return else isCameraMove = true

        location.let { mapManager?.moveCamera(it!!, 17f) }
        viewModel.hideLoading("LiveLocationFragment")
        startPostponedEnterTransition()
    }

    private fun exitSession() {
        viewModel.deleteMyLocation()
        requireActivity().supportFragmentManager.popBackStack()
    }


    override fun handleEvent(event: LiveLocationViewModel.Event) {
        when (event) {
            // 세션 유저 목록 업데이트
            is LiveLocationViewModel.Event.UsersUpdated -> {
                binding.liveLocationMarkerCountText.text = "${event.users.size}"
                markerManager.updateMarkers(event.users)
                // 타켓 카메라 추적
                if (markerManager.getMarkerPosition(targetUerId) != null) {
                    mapManager?.moveCamera(markerManager.getMarkerPosition(targetUerId)!!)
                } else {
                    binding.liveLocationChaseIcon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.icon_chase_off)
                    )
                    binding.liveLocationChaseNickname.text = "없음"
                    targetUerId = ""
                }
            }

            // 유효하지 않은 세션의 경우
            is LiveLocationViewModel.Event.InvalidSession -> {
                showCustomAlert(event.message)
                exitSession()
                viewModel.deleteSession()
            }

            else -> {}
        }
    }
}
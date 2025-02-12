package com.miiiin15.whereru.ui.location

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel {
            myLocation observe {
                mMap.let {
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(myLocation.value!!.latitude, myLocation.value!!.longitude),
                            18f
                        )
                    )
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
        val SEOUL = LatLng(37.556, 126.97)

        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")

        mMap!!.addMarker(markerOptions)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10f))
    }

    override fun handleEvent(event: LiveLocationViewModel.Event) {
    }
}
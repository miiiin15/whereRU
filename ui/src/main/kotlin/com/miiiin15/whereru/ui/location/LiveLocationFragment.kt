package com.miiiin15.whereru.ui.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.miiiin15.whereru.presentation.viewmodel.LiveLocationViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentLiveLocationBinding

class LiveLocationFragment: BaseFragment<FragmentLiveLocationBinding, LiveLocationViewModel, LiveLocationViewModel.Event>(
    R.layout.fragment_live_location) {
    override val viewModel: LiveLocationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun handleEvent(event: LiveLocationViewModel.Event) {
    }
}
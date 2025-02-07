package com.miiiin15.whereru.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miiiin15.whereru.presentation.viewmodel.HomeViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, HomeViewModel.Event>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showCustomAlert("앱을 종료하시겠습니까?") {
                        requireActivity().finish()
                    }
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }
}
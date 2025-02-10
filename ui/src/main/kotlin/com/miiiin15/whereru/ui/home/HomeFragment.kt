package com.miiiin15.whereru.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miiiin15.whereru.presentation.navigation.HomeNavigationTarget
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

        // 돌아 왔을때 내정보 조회
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                if (!viewModel.fetched.value!!) {
                    viewModel.fetchProfile()
                }
            }
        }

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding {
            vm = viewModel

            homeNavigateLocationButton.setOnClickListener {
                viewModel.checkSessionID()
            }
        }

        viewModel {
            myProfile observe { my ->
                binding.homeTitleText.text = my.nickname
            }

            navigationTarget observe { target ->
                when (target) {
                    HomeNavigationTarget.ToLiveLocation -> {
                        val action = HomeFragmentDirections.actionHomeToLiveLocation()
                        findNavController().navigate(action)
                        viewModel.clearTrigger()
                    }
                    HomeNavigationTarget.ToSetting -> {}
                    HomeNavigationTarget.ToProfileEdit -> {}
                    null -> {}
                }
            }
        }
    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }
}
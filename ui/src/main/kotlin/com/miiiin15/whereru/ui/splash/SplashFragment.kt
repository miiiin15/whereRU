package com.miiiin15.whereru.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.miiiin15.whereru.common.utils.PermissionManager
import com.miiiin15.whereru.presentation.viewmodel.AppViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, AppViewModel, AppViewModel.Event>(
        R.layout.fragment_splash
    ) {

    override val viewModel: AppViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        PermissionManager.requestPermissionsFromFragment(this) {
            proceedToNextScreen()
        }
    }

    private fun proceedToNextScreen() {
        // TODO: 다음 화면
    }

    override fun handleEvent(event: AppViewModel.Event) {}
}

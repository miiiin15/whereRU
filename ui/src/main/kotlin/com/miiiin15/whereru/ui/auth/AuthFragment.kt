package com.miiiin15.whereru.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miiiin15.whereru.presentation.viewmodel.AuthViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthFragment :
    BaseFragment<FragmentAuthBinding, AuthViewModel, AuthViewModel.Event>(
        R.layout.fragment_auth
    ) {
    override val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel {
            authState observe {
                if (it)
                    findNavController().navigate(AuthFragmentDirections.actionAuthToHome())
            }
        }
        binding {
            vm = viewModel
        }
    }

    override fun handleEvent(event: AuthViewModel.Event) {

    }
}
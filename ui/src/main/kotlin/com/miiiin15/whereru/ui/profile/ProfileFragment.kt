package com.miiiin15.whereru.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.miiiin15.whereru.presentation.viewmodel.ProfileViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentProfileBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel, ProfileViewModel.Event>(
        R.layout.fragment_profile
    ) {
    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel {
            selectedColor observe { color ->
                binding.profileColorPreview.setBackgroundColor(color)
            }

            myProfile observe { profile ->
                binding.profileNicknameInput.setText(profile.nickname)
                setSelectedColor(
                    profile.profileImageUrl?.toIntOrNull() ?: resources.getColor(R.color.gray1)
                )
            }
        }
        binding {
            vm = viewModel

            profileCloseButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            profileColorPreview.setOnClickListener {
                showColorPickerDialog()
            }

            profileColorPickerButton.setOnClickListener {
                showColorPickerDialog()
            }

            profileConfirmButton.setOnClickListener {
                viewModel.setProfile {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun showColorPickerDialog() {
        ColorPickerDialog.Builder(requireContext())
            .setTitle("컬러 선택")
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton("확인", ColorEnvelopeListener { envelope, _ ->
                val color = envelope.color // ← 진짜 Int값 (ARGB)
                viewModel.setSelectedColor(color)
            })
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(true) // 밝기 슬라이더 추가
            .setBottomSpace(12) // 하단 여백
            .show()
    }

    override fun handleEvent(event: ProfileViewModel.Event) {

    }
}
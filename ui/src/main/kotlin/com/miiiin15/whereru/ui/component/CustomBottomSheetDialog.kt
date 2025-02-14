package com.miiiin15.whereru.ui.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miiiin15.whereru.ui.databinding.BottomSheetDialogBinding

class CustomBottomSheetDialog(
    private val content: String?,
    private val buttonLeftText: String?,
    private val buttonRightText: String?,
    private val onLeftButtonClick: () -> Unit,
    private val onRightButtonClick: () -> Unit?
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDialogBinding.inflate(inflater, container, false).apply {
            root?.layoutParams?.height = (resources.displayMetrics.heightPixels * 0.4).toInt()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetContent.text = content
        binding.bottomSheetButtonLeft.text = buttonLeftText
        binding.bottomSheetButtonRight.text = buttonRightText

        binding.bottomSheetButtonLeft.setOnClickListener {
            onLeftButtonClick()
            dismiss()
        }

        binding.bottomSheetButtonRight.apply {
            if (onRightButtonClick == null) {
                visibility = View.GONE
                (binding.bottomSheetButtonLeft.layoutParams as ViewGroup.MarginLayoutParams).marginEnd =
                    0
            } else {
                setOnClickListener {
                    onRightButtonClick()
                    dismiss()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
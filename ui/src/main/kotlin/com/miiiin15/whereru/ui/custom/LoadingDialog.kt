package com.miiiin15.whereru.ui.custom

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.databinding.CustomLoadingBinding

class LoadingDialog : DialogFragment() {

    lateinit var binding: CustomLoadingBinding

    private var isProgress = false  // show()가 여러번 호출되는 것을 방지하기 위한 플래그

    override fun getTheme() = R.style.LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.custom_loading,
            container,
            false
        )

        binding.lifecycleOwner = this

        binding.loadingImageView.let {
            Glide.with(this).load(R.raw.load).into(it)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setCancelable(false)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isProgress) {
            isProgress = true
            try {
                super.show(manager, tag)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun dismiss() {
        if (isProgress) {
            isProgress = false
            super.dismissAllowingStateLoss()
        }
    }
}
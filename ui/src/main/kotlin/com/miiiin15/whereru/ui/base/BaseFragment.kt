package com.miiiin15.whereru.ui.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.extension.observe
import com.miiiin15.whereru.ui.extension.repeatOnStarted
import kotlinx.coroutines.flow.StateFlow
import com.miiiin15.whereru.ui.BR
import com.miiiin15.whereru.ui.custom.LoadingDialog

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int,
) : Fragment() {
    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: throw IllegalStateException("fragment destroyed!")

    private val loadingDialog by lazy { LoadingDialog() }

    abstract val viewModel: VM
    abstract fun handleEvent(event: VE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding {
            lifecycleOwner = viewLifecycleOwner
            setVariable(BR.viewModel, viewModel)
            setVariable(BR.view, this@BaseFragment)
        }
        observeEvent()
        observeLoading()
        observeAlert()
    }

    private fun observeEvent() = repeatOnStarted {
        viewModel.eventFlow
            .collect { handleEvent(it) }
    }

    private fun observeLoading() = repeatOnStarted {
        viewModel.loading.observe { isLoading ->
            if (isLoading) loadingDialog.show(childFragmentManager, null)
            else loadingDialog.dismiss()
        }
    }

    private fun observeAlert() = repeatOnStarted {
        viewModel.alertMessage.observe {
            if (it.isNotBlank()) {
                showCustomAlert(it)
            }
        }
    }

    fun showCustomAlert(message: String) {
        AlertDialog.Builder(requireContext()).setOnDismissListener {
            viewModel.clearAlert()
        }
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // binding. 을 생략하고 블록 안에서 객체 속성을 직접 접근할 수 있게
    protected fun binding(action: B.() -> Unit) {
        binding.run(action)
    }

    // viewModel. 을 생략하고 블록 안에서 객체 속성을 직접 접근할 수 있게
    protected fun viewModel(action: VM.() -> Unit) {
        viewModel.run(action)
    }

    protected infix fun <T> StateFlow<T?>.observe(action: (T) -> Unit) {
        observe(viewLifecycleOwner, action)
    }
}
package com.miiiin15.whereru.ui.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<B : ViewDataBinding, out D>(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
) {
    protected val binding: B = DataBindingUtil.bind(itemView)!!
    protected val context: Context = parent.context

    abstract fun setData(data: @UnsafeVariance D)

    open fun recycled() {
        // no-op
    }

}

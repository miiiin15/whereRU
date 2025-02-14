package com.miiiin15.whereru.ui.home

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.adapter.BaseAdapter
import com.miiiin15.whereru.ui.base.adapter.BaseViewHolder
import com.miiiin15.whereru.ui.databinding.ItemSessionListBinding

class SessionListAdapter() :
    BaseAdapter<LocationSessionUiModel, SessionListAdapter.BaseSessionListAdapter<out ViewDataBinding>>() {

    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionListAdapter.BaseSessionListAdapter<out ViewDataBinding> =
        LinearSessionListViewHolder(parent)
    private var emptyView: LinearLayout? = null

    override fun resetAll(items: List<LocationSessionUiModel>) {
        val diffCallback = SessionDiffCallback(this.items, items)
        resetAll(items, diffCallback)
        checkEmptyView()
    }

    fun setEmptyView(view: LinearLayout) {
        emptyView = view
        checkEmptyView()
    }

    private fun checkEmptyView() {
        if (emptyView != null) {
            if (items.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView!!.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView!!.visibility = View.GONE
            }
        }
    }


    inner class LinearSessionListViewHolder(parent: ViewGroup) :
        BaseSessionListAdapter<ItemSessionListBinding>(parent, R.layout.item_session_list) {
        override val sessionBinding: ItemSessionListBinding
            get() = binding
    }

    abstract class BaseSessionListAdapter<B : ViewDataBinding>(
        parent: ViewGroup,
        layoutResId: Int
    ) :
        BaseViewHolder<B, LocationSessionUiModel>(
            parent,
            layoutResId
        ) {
        abstract val sessionBinding: ItemSessionListBinding

        init {
            sessionBinding.root.setOnClickListener {
                println("🔆 : ${sessionBinding.sessionInfo}")
            }
        }

        override fun setData(data: LocationSessionUiModel) {
            sessionBinding.sessionInfo = data
            sessionBinding.executePendingBindings()
            // TODO : Glide로 프로필 이미지 로딩
        }

        fun showAlert() {
            // TODO : 후속 액션 안내 창 띄우기
        }
    }
}
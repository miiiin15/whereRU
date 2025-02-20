package com.miiiin15.whereru.ui.home

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.adapter.BaseAdapter
import com.miiiin15.whereru.ui.base.adapter.BaseViewHolder
import com.miiiin15.whereru.ui.databinding.ItemUserListBinding

class UserListAdapter(
    private val onClickListener: OnUserItemClickListener
) :
    BaseAdapter<UserUiModel, UserListAdapter.BaseUserListAdapter<out ViewDataBinding>>() {
    private var emptyView: LinearLayout? = null


    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListAdapter.BaseUserListAdapter<out ViewDataBinding> =
        LinearUserListViewHolder(parent)

    override fun resetAll(items: List<UserUiModel>) {
        val diffCallback = UserDiffCallback(this.items, items)
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

    inner class LinearUserListViewHolder(parent: ViewGroup) :
        BaseUserListAdapter<ItemUserListBinding>(parent, R.layout.item_user_list, onClickListener) {
        override val userBinding: ItemUserListBinding
            get() = binding
    }

    abstract class BaseUserListAdapter<B : ViewDataBinding>(
        parent: ViewGroup,
        layoutResId: Int,
        onClickListener: OnUserItemClickListener
    ) :
        BaseViewHolder<B, UserUiModel>(
            parent,
            layoutResId
        ) {
        abstract val userBinding: ItemUserListBinding

        init {
            userBinding.root.setOnClickListener {
                onClickListener.onUserItemClick(userBinding.userInfo!!)
            }
        }

        override fun setData(data: UserUiModel) {
            userBinding.userInfo = data
            userBinding.executePendingBindings()
            if(!data.profileImageUrl.isNullOrBlank()){
            userBinding.userItemProfileImage.backgroundTintList =
                ColorStateList.valueOf(data.profileImageUrl!!.toInt())
            }
        }

    }
}
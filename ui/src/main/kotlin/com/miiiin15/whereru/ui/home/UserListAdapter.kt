package com.miiiin15.whereru.ui.home

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.adapter.BaseAdapter
import com.miiiin15.whereru.ui.base.adapter.BaseViewHolder
import com.miiiin15.whereru.ui.databinding.ItemUserListBinding

class UserListAdapter() :
    BaseAdapter<UserUiModel, UserListAdapter.BaseUserListAdapter<out ViewDataBinding>>() {

    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListAdapter.BaseUserListAdapter<out ViewDataBinding> =
        LinearSessionListViewHolder(parent)

    override fun resetAll(items: List<UserUiModel>) {
        val diffCallback = UserDiffCallback(this.items, items)
        resetAll(items, diffCallback)
    }

    inner class LinearSessionListViewHolder(parent: ViewGroup) :
        BaseUserListAdapter<ItemUserListBinding>(parent, R.layout.item_user_list) {
        override val sessionBinding: ItemUserListBinding
            get() = binding
    }

    abstract class BaseUserListAdapter<B : ViewDataBinding>(
        parent: ViewGroup,
        layoutResId: Int
    ) :
        BaseViewHolder<B, UserUiModel>(
            parent,
            layoutResId
        ) {
        abstract val sessionBinding: ItemUserListBinding

        init {
            sessionBinding.root.setOnClickListener {
                println("🔆 : ${sessionBinding.userInfo}")
            }
        }

        override fun setData(data: UserUiModel) {
            sessionBinding.userInfo = data
            sessionBinding.executePendingBindings()
            // TODO : Glide로 프로필 이미지 로딩
        }

        fun showAlert() {
            // TODO : 후속 액션 안내 창 띄우기
        }
    }
}
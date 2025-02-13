package com.miiiin15.whereru.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.miiiin15.whereru.presentation.model.UserUiModel

class UserDiffCallback(
    private val oldList: List<UserUiModel>,
    private val newList: List<UserUiModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].userId == newList[newItemPosition].userId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
package com.miiiin15.whereru.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.miiiin15.whereru.presentation.model.JoinedSessionUiModel

class SessionDiffCallback(
    private val oldList: List<JoinedSessionUiModel>,
    private val newList: List<JoinedSessionUiModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].sessionId == newList[newItemPosition].sessionId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
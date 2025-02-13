package com.miiiin15.whereru.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel

class SessionDiffCallback(
    private val oldList: List<LocationSessionUiModel>,
    private val newList: List<LocationSessionUiModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].hostId == newList[newItemPosition].hostId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
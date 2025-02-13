package com.miiiin15.whereru.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel
import com.miiiin15.whereru.ui.R

class SessionListItemAdapter(private val items: List<LocationSessionUiModel>) : RecyclerView.Adapter<SessionListItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val sessionInfo: TextView = view.findViewById(R.id.session_list_item_info_text)

        fun bind(session: LocationSessionUiModel) {
            sessionInfo.text = "Host: ${session.hostId}\nActive: ${session.isActive}\nStarted At: ${session.startedAt}"
        }
    }
}
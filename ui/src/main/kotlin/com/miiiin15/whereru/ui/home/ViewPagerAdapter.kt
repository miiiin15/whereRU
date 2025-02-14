package com.miiiin15.whereru.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.miiiin15.whereru.ui.R

class ViewPagerAdapter(
    private val fragment: HomeFragment
) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scroll_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 3

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recyclerView: RecyclerView = view.findViewById(R.id.home_list_recycler)
        private val emptyView: LinearLayout = view.findViewById(R.id.home_list_empty_view)

        fun bind(position: Int) {
            fragment.bind(position, recyclerView, emptyView)
        }
    }
}
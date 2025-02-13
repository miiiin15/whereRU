package com.miiiin15.whereru.ui.base.adapter

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<D, VH : BaseViewHolder<out ViewDataBinding, out D>> :
    RecyclerView.Adapter<VH>() {

    private var _items: MutableList<D> = mutableListOf()
    val items: List<D> get() = _items
    lateinit var recyclerView: RecyclerView

    // 리사이클러 뷰에 어댑터가 연결될 때 호출
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    // 특정 인덱스의 아이템을 반환
    fun getItem(index: Int): D = items[index]

    // 아이템 리스트를 초기화하고 전체 갱신
    open fun resetAll(items: List<D>) {
        this._items.clear()
        this._items.addAll(items)
        notifyDataSetChanged()
    }

    // DiffUtil을 사용하여 아이템 리스트를 초기화하고 효율적으로 갱신
    protected fun resetAll(items: List<D>, diffCallback: DiffUtil.Callback) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager == null) {
            resetAll(items)
            return
        }
        val savedInstanceState = layoutManager.onSaveInstanceState()
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this._items.clear()
        this._items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
        layoutManager.onRestoreInstanceState(savedInstanceState)
    }

    // ViewHolder를 생성하는 추상 메서드
    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH

    // ViewHolder를 생성할 때 호출
    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = getViewHolder(parent, viewType)
        return holder
    }

    // ViewHolder에 데이터를 바인딩할 때 호출
    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindView(holder, getItem(position), position)
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    protected open fun onBindView(holder: VH, item: D, position: Int) {
        (holder as? BaseViewHolder<*, D>)?.setData(item)
    }

    // ViewHolder가 재활용될 때 호출
    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.recycled()
    }

    // 아이템의 총 개수를 반환
    override fun getItemCount(): Int = items.size

}
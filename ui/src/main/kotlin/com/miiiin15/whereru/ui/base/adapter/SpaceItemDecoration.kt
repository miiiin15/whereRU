package com.miiiin15.whereru.ui.base.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class SpaceItemDecoration(private val space: Int, private val includeEdge: Boolean = false) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                setGridLayoutSpace(layoutManager, outRect, position)
            }

            is StaggeredGridLayoutManager -> {
                setStaggeredGridLayoutSpace(layoutManager, view, outRect, position)
            }

            is LinearLayoutManager -> {
                setLinearLayoutSpace(layoutManager, outRect, position)
            }
        }

    }

    private fun setGridLayoutSpace(
        layoutManager: GridLayoutManager,
        outRect: Rect,
        position: Int
    ) {
        val spanCount = layoutManager.spanCount
        val column = position % spanCount // 현재 아이템의 열 위치를 계산
        setGridSpace(layoutManager.orientation, outRect, column, spanCount, position)
    }

    private fun setStaggeredGridLayoutSpace(
        layoutManager: StaggeredGridLayoutManager,
        view: View,
        outRect: Rect,
        position: Int
    ) {
        val spanCount = layoutManager.spanCount
        val column =
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex // 현재 아이템의 열 위치를 계산
        setGridSpace(layoutManager.orientation, outRect, column, spanCount, position)
    }

    private fun setGridSpace(
        @RecyclerView.Orientation orientation: Int,
        outRect: Rect,
        column: Int,
        spanCount: Int,
        position: Int
    ) {
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                if (includeEdge) {
                    outRect.apply {
                        // 아이템의 상단 간격을 설정
                        top = space - column * space / spanCount
                        // 아이템의 하단 간격을 설정
                        bottom = (column + 1) * space / spanCount
                        if (position < spanCount) {
                            // 첫 번째 행의 아이템에 좌측 간격을 추가
                            left = space
                        }
                        // 모든 아이템에 우측 간격을 추가
                        right = space
                    }
                } else {
                    outRect.apply {
                        // 아이템의 상단 간격을 설정
                        top = column * space / spanCount
                        // 아이템의 하단 간격을 설정
                        bottom = space - (column + 1) * space / spanCount
                        if (position >= spanCount) {
                            // 첫 번째 행을 제외한 모든 아이템에 좌측 간격을 추가
                            left = space
                        }
                    }
                }
            }

            RecyclerView.VERTICAL -> {
                if (includeEdge) {
                    outRect.apply {
                        // 아이템의 좌측 간격을 설정
                        left = space - column * space / spanCount
                        // 아이템의 우측 간격을 설정
                        right = (column + 1) * space / spanCount
                        if (position < spanCount) {
                            // 첫 번째 행의 아이템에 상단 간격을 추가
                            top = space
                        }
                        // 모든 아이템에 하단 간격을 추가
                        bottom = space
                    }
                } else {
                    outRect.apply {
                        // 아이템의 좌측 간격을 설정
                        left = column * space / spanCount
                        // 아이템의 우측 간격을 설정
                        right = space - (column + 1) * space / spanCount
                        if (position >= spanCount) {
                            // 첫 번째 행을 제외한 모든 아이템에 상단 간격을 추가
                            top = space
                        }
                    }
                }
            }
        }
    }

    private fun setLinearLayoutSpace(
        layoutManager: LinearLayoutManager,
        outRect: Rect,
        position: Int
    ) {
        when (layoutManager.orientation) {
            RecyclerView.HORIZONTAL -> {
                if (includeEdge) {
                    outRect.apply {
                        if (position < 1) {
                            // 첫 번째 아이템에 좌측 간격을 추가
                            left = space
                        }
                        // 모든 아이템에 상단, 하단, 우측 간격을 추가
                        top = space
                        bottom = space
                        right = space
                    }
                } else {
                    outRect.apply {
                        if (position >= 1) {
                            // 첫 번째 아이템을 제외한 모든 아이템에 좌측 간격을 추가
                            left = space
                        }
                    }
                }
            }

            RecyclerView.VERTICAL -> {
                if (includeEdge) {
                    outRect.apply {
                        if (position < 1) {
                            // 첫 번째 아이템에 상단 간격을 추가
                            top = space
                        }
                        // 모든 아이템에 좌측, 우측, 하단 간격을 추가
                        left = space
                        right = space
                        bottom = space
                    }
                } else {
                    outRect.apply {
                        if (position >= 1) {
                            // 첫 번째 아이템을 제외한 모든 아이템에 상단 간격을 추가
                            top = space
                        }
                    }
                }
            }
        }

    }

}
package com.miiiin15.whereru.ui.component

import androidx.annotation.CallSuper
import androidx.constraintlayout.motion.widget.MotionLayout

open class SimpleMotionLayoutListener : MotionLayout.TransitionListener {
    private var lastProgress = 0f

    override fun onTransitionTrigger(
        parent: MotionLayout,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) = Unit

    @CallSuper
    override fun onTransitionStarted(
        parent: MotionLayout,
        startId: Int,
        endId: Int
    ) {
        lastProgress = 0f
    }

    @CallSuper
    final override fun onTransitionChange(
        parent: MotionLayout,
        startId: Int,
        endId: Int,
        progress: Float
    ) {
        onTransitionChange(parent, startId, endId, progress, progress > lastProgress)
        lastProgress = progress
    }

    open fun onTransitionChange(
        parent: MotionLayout,
        startId: Int,
        endId: Int,
        progress: Float,
        positive: Boolean
    ) = Unit

    override fun onTransitionCompleted(
        parent: MotionLayout,
        currentId: Int
    ) = Unit
}

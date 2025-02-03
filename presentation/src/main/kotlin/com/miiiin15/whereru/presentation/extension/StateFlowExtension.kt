package com.miiiin15.whereru.presentation.extension
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.StateFlow

// StateFlow를 LiveData처럼 사용하기 위해 만든 함수
fun <T> StateFlow<T?>.observe(lifecycleOwner: LifecycleOwner, action: (value: T) -> Unit) =
    // lanchWhenStarted는 lifecycleOwner의 상태가 STARTED일 때만 실행
    // TODO : repeatOnLifecycle 사용 권장 (https://developer.android.com/topic/libraries/architecture/coroutines)
    lifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
        collect { action(it ?: return@collect) }
    }
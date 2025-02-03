package com.miiiin15.whereru.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.data_resource.collectDataResource
import com.miiiin15.whereru.data_resource.mapDataResource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<VE : ViewEvent> : ViewModel() {

    private val loadingCountMap = mutableMapOf<String, Int>() // 로딩 상태를 관리하기 위한 Map
    private val jobMap: MutableMap<String, Job> = mutableMapOf() // Job을 관리하기 위한 Map

    private val _loading = MutableStateFlow(false) // 로딩 상태를 나타내는 StateFlow
    val loading = _loading.asStateFlow() // 로딩 상태를 나타내는 StateFlow를 외부에서 접근할 수 있도록 함

    private val _eventChannel: Channel<VE> = Channel() // ViewEvent를 전달하기 위한 Channel
    val eventFlow = _eventChannel.receiveAsFlow() // ViewEvent를 전달하기 위한 Channel을 외부에서 접근할 수 있도록 함

    fun showLoading(tag: String = DEFAULT_LOADING_TAG) {
        if (loadingCountMap.values.sum() == 0) { // 로딩 상태가 0이면
            _loading.value = true // 로딩 상태를 true로 변경
        }
        loadingCountMap[tag] =
            (loadingCountMap[tag] ?: 0) + 1 // 로딩 상태를 관리하는 Map에 tag에 해당하는 값을 1 증가시킴
    }

    fun hideLoading(tag: String = DEFAULT_LOADING_TAG) {
        loadingCountMap[tag] =
            (loadingCountMap[tag] ?: 0) - 1 // 로딩 상태를 관리하는 Map에 tag에 해당하는 값을 1 감소시킴
        if (loadingCountMap.values.sum() == 0) {
            _loading.value = false
        }
    }

    fun clearLoading(tag: String = DEFAULT_LOADING_TAG) {
        loadingCountMap[tag] = 0 // 로딩 상태를 관리하는 Map에 tag에 해당하는 값을 0으로 초기화
        if (loadingCountMap.values.sum() == 0) {
            _loading.value = false
        }
    }

    open fun handleError(throwable: Throwable?) {
        handleError(throwable, DEFAULT_LOADING_TAG)
    }

    fun handleError(throwable: Throwable?, tag: String) {
        throwable?.printStackTrace()
        hideLoading(tag)
        // TODO: 에러 처리 로직 추가 ex) log, toast, snackbar, etc.
    }

    // Flow<DataResource<T>>를 구독하여 데이터를 처리하는 확장 함수
    protected fun <Domain, Presentation> Flow<DataResource<Domain>>.stateFlow(
        defaultValue: Presentation,
        mapper: (Domain) -> Presentation
    ): StateFlow<Presentation> {
        val flow = MutableStateFlow(defaultValue)
        launch {
            mapDataResource(mapper)
                .collectDataResource({ flow.emit(it) })
        }
        return flow.asStateFlow()
    }

    override fun onCleared() {
        super.onCleared()
        jobMap.clear()
    }


    protected suspend fun <T> Flow<DataResource<T>>.collectDataResource(
        onSuccess: suspend (T) -> Unit,
        loadingEnable: Boolean = true,
        loadingTag: String = DEFAULT_LOADING_TAG,
        onError: (Throwable) -> Unit = {
            if (loadingEnable) {
                hideLoading(loadingTag)
            }
            handleError(it)
        },
        onLoading: (T?) -> Unit = {
            if (loadingEnable) {
                showLoading(loadingTag)
            }
        },
    ) {
        return collectDataResource({
            if (loadingEnable) {
                hideLoading(loadingTag)
            }
            onSuccess(it)
        }, onError, onLoading)
    }

    protected suspend fun <T> Flow<DataResource<T>>.await(): T? {
        var data: T? = null
        collectDataResource({
            data = it
        })
        return data
    }

    protected fun <T> Flow<DataResource<T>>.assign(data: MutableStateFlow<T>) = launch {
        data.value = await() ?: return@launch
    }

    protected inline fun <T, R> StateFlow<T?>.map(crossinline transform: (value: T) -> R): StateFlow<R?> =
        mapLatest { value -> value?.let { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    protected inline fun <T, R> StateFlow<List<T>>.mapList(crossinline transform: (value: T) -> R): StateFlow<List<R>> =
        mapLatest { list -> list.map { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    protected inline fun <T> StateFlow<List<T>>.mapFilter(crossinline transform: (value: T) -> Boolean): StateFlow<List<T>> =
        mapLatest { list -> list.filter { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, exception -> handleError(exception) }

    // 이 함수는 lanch 함수에 context와 start를 추가한 함수
    private fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(context + coroutineExceptionHandler, start, block)

    /**
     * @param tag [DEFAULT_JOB_TAG]가 아닌 경우 동일한 tag에 Job이 있다면 취소 후 다시 실행한다.
     */
    protected fun launch(
        tag: String = DEFAULT_JOB_TAG,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        if (tag == DEFAULT_JOB_TAG) {
            return launch(context, start, block) // tag가 DEFAULT_JOB_TAG인 경우 launch 함수를 호출
        }
        jobMap[tag]?.cancel() // tag에 해당하는 Job이 있다면 취소
        return launch(context, start, block).apply {
            jobMap[tag] = this
        }
    }

    open fun event(event: VE): Job = launch {
        _eventChannel.send(event)
    }

    // 로딩 상태 구분용
    companion object {
        const val DEFAULT_LOADING_TAG = "DEFAULT_LOADING_TAG"
        const val DEFAULT_JOB_TAG = "DEFAULT_JOB_TAG"
    }
}
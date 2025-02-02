package com.miiiin15.whereru.data_resource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

// Flow<DataResource<T>>를 구독하여 데이터를 처리하는 확장 함수
suspend fun <T> Flow<DataResource<T>>.collectDataResource(
    onSuccess: suspend (T) -> Unit,
    onError: (Throwable) -> Unit,
    onLoading: (T?) -> Unit = {},
) {
    this.catch { onError(it) }
        .collect {
            when (it) {
                is DataResource.Success -> onSuccess(it.data)
                is DataResource.Error -> onError(it.throwable)
                is DataResource.Loading -> onLoading.invoke(it.data)
            }
        }
}

// onSucess, onError는 onEach를 사용하여 DataResource의 상태에 따라 처리
fun <T> Flow<DataResource<T>>.onSuccess(action: suspend (T) -> Unit): Flow<DataResource<T>> =
    onEach { resource ->
        if (resource is DataResource.Success<T>) {
            action(resource.data)
        }
    }

fun <T> Flow<DataResource<T>>.onError(action: suspend (Throwable) -> Unit): Flow<DataResource<T>> =
    onEach { resource ->
        if (resource is DataResource.Error) {
            action(resource.throwable)
        }
    }


// transform을 사용해 성공 시 첫번째 데이터를 반환하고 에러 시 예외를 던짐
suspend fun <T> Flow<DataResource<T>>.awaitOrThrow(): T =
    transform { resource ->
        when (resource) {
            is DataResource.Success -> emit(resource.data)
            is DataResource.Error -> throw resource.throwable
            is DataResource.Loading -> return@transform
        }
    }.first()

// 실패, 로딩시에는 그대로 반환하고 성공시에만 미리 정의된 mapper를 사용해 데이터를 변환
fun <T, R> Flow<DataResource<T>>.mapDataResource(mapper: (T) -> R)
    : Flow<DataResource<R>> =
    map {
        when (it) {
            is DataResource.Success -> DataResource.success(mapper(it.data))
            is DataResource.Error -> DataResource.error(it.throwable)
            is DataResource.Loading -> {
                val newData = it.data?.let { source -> mapper(source) }
                DataResource.loading(newData)
            }
        }
    }

// 리스트를 매핑하는 확장함수
fun <T, R> Flow<DataResource<List<T>>>.mapListDataResource(mapper: (T) -> R)
    : Flow<DataResource<List<R>>> =
    mapDataResource { list -> list.map(mapper) }


// 로컬 또는 리모트중 하나의 데이터를 먼저 가져오고 그 데이터를 가지고 다시 데이터를 가져오는 경우 사용
fun <T, R> Flow<DataResource<T>>.flatMapDataResource(operation: (T) -> Flow<DataResource<R>>): Flow<DataResource<R>> =
    transform { resource ->
        when (resource) {
            is DataResource.Success -> emitAll(operation(resource.data))
            is DataResource.Error -> emit(DataResource.error(resource.throwable))
            is DataResource.Loading -> {
                val data = resource.data?.let(operation) ?: return@transform
                emitAll(data)
            }
        }
    }

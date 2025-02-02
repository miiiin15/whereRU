package com.miiiin15.whereru.data_resource

// 데이터를 로딩, 성공, 실패 상태로 나타내는 클래스
sealed class DataResource<out T> {
    class Success<T>(val data: T) : DataResource<T>()
    class Error(val throwable: Throwable) : DataResource<Nothing>()
    class Loading<T>(val data: T? = null) : DataResource<T>() // 로딩 중 데이터를 포함할 수도 있음

    companion object {
        // 성공한 데이터(data)를 포함
        fun <T> success(data: T) = Success(data)
        // 예외(throwable)를 포함해 에러 처리
        fun error(throwable: Throwable) = Error(throwable)
        // 로딩 중 데이터를 포함
        fun <T> loading(data: T? = null): Loading<T> = Loading(data)

    }
}
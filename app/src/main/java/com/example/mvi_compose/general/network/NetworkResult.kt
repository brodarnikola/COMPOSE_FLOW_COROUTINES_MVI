package com.example.mvi_compose.general.network

sealed class NetworkResult<T : Any> {
    class Success<T: Any>(val data: T) : NetworkResult<T>()
    class Error<T: Any>(val code: Int, val message: String?, val apiError: ApiError?) : NetworkResult<T>()
    class Exception<T: Any>(val e: Throwable) : NetworkResult<T>()
}

data class ApiError(
    val message: String,
    val error: ErrorInfo,
    val additionalInfo: String,
)

data class ErrorInfo(
    val message: String,
    val name: String,
    val status: Int,
    val type: String
)

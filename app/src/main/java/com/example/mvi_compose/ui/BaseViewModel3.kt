package com.example.mvi_compose.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

abstract class SecondBaseViewModel<T> : ViewModel() {

    protected val _state: MutableState<Resource<T>> = mutableStateOf(Resource.Loading())
    val state: State<Resource<T>>
        get() = _state

    protected abstract fun initialState(): T
}

sealed class Resource<T> {
    data class Success<T>(val data: T? = null) : Resource<T>() {
        override fun toString() = "[Success: $data]"
    }

    // Optional data allows to expose data stub just for loading state.
    data class Loading<T>(val data: T? = null) : Resource<T>() {
        override fun toString() = "[Loading: $data]"
    }
    data class Initial<T>(val data: T? = null) : Resource<T>() {
        override fun toString() = "[Loading: $data]"
    }

    data class Error<T>(val error: T? = null) : Resource<T>() {
        override fun toString() = "[Failure: $error]"
    }

    fun unwrap(): T? =
        when (this) {
            is Loading -> data
            is Initial -> data
            is Success -> data
            is Error -> error
        }

}
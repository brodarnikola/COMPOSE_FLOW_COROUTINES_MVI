package com.example.mvi_compose.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

abstract class SecondBaseViewModel<StateType, Event> : ViewModel() {

    protected val _state: MutableState<Resource<StateType>> = mutableStateOf(Resource.Loading())
    val state: State<Resource<StateType>>
        get() = _state

    protected abstract fun initialState(): StateType

    abstract fun onEvent(event: Event)
}

sealed class Resource<StateType> {
    data class Success<StateType>(val data: StateType? = null) : Resource<StateType>() {
        override fun toString() = "[Success: $data]"
    }

    // Optional data allows to expose data stub just for loading state.
    data class Loading<StateType>(val data: StateType? = null) : Resource<StateType>() {
        override fun toString() = "[Loading: $data]"
    }

    data class Error<StateType>(val error: StateType? = null) : Resource<StateType>() {
        override fun toString() = "[Failure: $error]"
    }

    fun unwrap(): StateType? =
        when (this) {
            is Loading -> data
            is Success -> data
            is Error -> error
        }

}
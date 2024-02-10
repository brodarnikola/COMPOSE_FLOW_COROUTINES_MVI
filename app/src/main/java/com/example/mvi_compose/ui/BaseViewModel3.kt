package com.example.mvi_compose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Model
//interface State
//
//// Intent
//interface Intent

// ViewModel
abstract class BaseViewModel3<StateType, EventType> : ViewModel() {
    protected val _state = MutableStateFlow(this.createInitialState()) // main state
    val state = _state.asStateFlow()

    private val _intentChannel = MutableSharedFlow<EventType>()
    val intentChannel = _intentChannel.asSharedFlow()

    init {
        viewModelScope.launch {
            handleIntents()
        }
    }

    protected abstract fun createInitialState(): StateType

    protected abstract suspend fun handleIntent(intent: EventType)

    private suspend fun handleIntents() {
        intentChannel.collect { intent ->
            handleIntent(intent)
        }
    }

//    protected fun setState(state: StateType) {
//        _state.value = state
//    }
//
//    protected fun sendIntent(intent: EventType) {
//        viewModelScope.launch {
//            _intentChannel.emit(intent)
//        }
//    }
}
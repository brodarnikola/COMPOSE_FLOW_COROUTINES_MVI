package com.example.mvi_compose.ui

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<StateType, ScreenEventType> : ViewModel() {

    protected val _state = MutableStateFlow(this.initialState()) // main state
    val state = _state.asStateFlow()
    protected abstract fun initialState(): StateType // set initial state value

    /** for receiving events from UI **/
    abstract fun onEvent(event: ScreenEventType)

    /** for sending one time channel events to UI **/
    private val _uiEffectChannel = Channel<UiEffect>()
    val uiEffect = _uiEffectChannel.receiveAsFlow()

    protected fun sendUiEvent(event: UiEffect) {
        viewModelScope.launch {
            _uiEffectChannel.send(event)
        }
    }
}

interface UiEffect {
//    data class Error(val message: String): UiEffect //
     data class ShowToast(val message: String, val toastLength: Int = Toast.LENGTH_SHORT) : UiEffect
//    data class ShowDialog(val message: String): UiEffect
//    data class Navigate(val route: String): UiEffect
}
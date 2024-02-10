package com.example.mvi_compose.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Modify CounterViewModel to extend BaseViewModel
class CounterViewModel : BaseViewModel<CounterState, CounterEvent>() {


    override fun initialState(): CounterState {
        return CounterState()
    }

    override fun onEvent(event: CounterEvent) {
        when(event) {
            is CounterEvent.IncrementEvent -> {
                _state.update {
                    it.copy(count = it.count + 1)
                }
                sendUiEvent(UiEffect.ShowToast(message = "Incremented by one"))
            }
            is CounterEvent.DecrementEvent -> {
                _state.update {
                    it.copy(count = it.count - 1)
                }
                sendUiEvent( UiEffect.ShowToast(message = "Decremented by one"))
            }
        }
    }

    // Process UI events
//    override fun handleEvent(event: CounterEvent) {
//        when (event) {
//            is CounterEvent.IncrementEvent -> updateCounterState(currentState.copy(count = currentState.count + 1))
//            is CounterEvent.DecrementEvent -> updateCounterState(currentState.copy(count = currentState.count - 1))
//        }
//    }

    // Additional business logic or side effects can be handled here

    // Example of handling a side effect
    private fun showToast(message: String) {
        viewModelScope.launch {
//            _effect.send(CounterEffect.ShowToastEffect(message))
        }
    }

    private fun updateCounterState(newState: CounterState) {
//        _uiState.value = newState
    }

}

sealed class CounterEvent {
    object IncrementEvent : CounterEvent()
    object DecrementEvent : CounterEvent()
}

data class CounterState(val count: Int = 0)

//sealed class CounterEffect : UiEffect {
//    data class ShowToastEffect(val message: String) : CounterEffect()
//}
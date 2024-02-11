package com.example.mvi_compose.ui

import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.movies_list.Movie
import com.example.mvi_compose.movies.movies_list.MovieRepo
import com.example.mvi_compose.movies.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val movieRepo: MovieRepo
) : BaseViewModel<CounterState, CounterEvent>() {

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

    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            when(val result = movieRepo.getPopularMovies()) {

                is NetworkResult.Error -> {

                }
                is NetworkResult.Exception -> {

                }
                is NetworkResult.Success -> {
                    val test9 = result.data.results

                    _state.update { it.copy(loading = false, movies = result.data.results) }
                }

//                is NetworkResult.Error -> {
//                    if( engineStatusCounter < 15 ) {
//                        delay(2000)
//                        engineStatusCounter++
//                        checkEngineStatusCounter(messagingEngine, engineUid)
//                    }
//                    Timber.d("getAESKey() error - ${result.code}: ${result.message}")
//                }
//                is NetworkResult.Exception -> {
//                    if( engineStatusCounter < 15 ) {
//                        delay(2000)
//                        engineStatusCounter++
//                        checkEngineStatusCounter(messagingEngine, engineUid)
//                    }
//                    Timber.d("getAESKey() exception error - ${result.e.localizedMessage}")
//                }
//                is NetworkResult.Success -> {
////                    if( result.data.state == "disconnected") {
////                        _state.update { it.copy(loading = false) }
////                    }
////                    else {
////                        if( engineStatusCounter < 15 ) {
////                            delay(2000)
////                            engineStatusCounter++
////                            checkEngineStatusCounter(messagingEngine, engineUid)
////                        }
////                    }
//                    _state.update { it.copy(loading = false) }
//                }
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

data class CounterState(val count: Int = 0, val movies: List<Movie> = listOf(), val loading: Boolean = false, val error: String = "")

//sealed class CounterEffect : UiEffect {
//    data class ShowToastEffect(val message: String) : CounterEffect()
//}
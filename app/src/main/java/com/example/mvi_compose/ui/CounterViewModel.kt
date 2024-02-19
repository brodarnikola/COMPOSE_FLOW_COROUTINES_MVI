package com.example.mvi_compose.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.movies_list.Movie
import com.example.mvi_compose.movies.movies_list.MovieRepo
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.utils.AppConstants.Companion.REST_API_CALL
import com.example.mvi_compose.movies.utils.MovieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val movieRepo: MovieRepo,
    private val movieDao: MovieDao
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
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(loading = true) }
            when(val result = movieRepo.getPopularMovies()) {

                is NetworkResult.Error -> {

                }
                is NetworkResult.Exception -> {

                }
                is NetworkResult.Success -> {
                    withContext(Dispatchers.Main) {
                        _state.update {
                            it.copy(
                                loading = false,
                                movies = result.data.results.toMutableStateList()
                            )
                        }  //, movies = result.data.results) }
                    }
//                    _state.value.movies.addAll( result.data.results ) // .toMutableList()

//                    _state.value = _state.value.copy(movies = result.data.results.toMutableStateList())

                        val listFetchImages = mutableListOf<Deferred<Unit>>()
                        result.data.results.forEachIndexed { index, movie ->
                            if( movieDao.fetchFavouriteMovies().isEmpty() ) {
                                withContext(Dispatchers.IO) {
                                    movieDao.insertMovie(movie)
                                }
                            }

                            listFetchImages.add(
                                async {
                                    val increment = if (index % 2 == 0) 3000 else 1500
                                    val random = Random.nextInt(500) + increment
                                    Log.d(
                                        REST_API_CALL,
                                        "Random delay is START: ${random} .. ${_state.value.movies[index]}"
                                    )
                                    delay(random.toLong())
                                    result.data.results.set(
                                        index,
                                        result.data.results[index].copy(random_delay = random.toLong())
                                    )
                                    Log.d(
                                        REST_API_CALL,
                                        "Random delay is FINISH: ${_state.value.movies[index]}"
                                    )
//                                    withContext(Dispatchers.Main) {
//                                        _state.update { it.copy(movies = result.data.results) }
//                                        _state.value.movies = result.data.results

//                                        _state.value.movies.value[index] = test9

//                                        _state.value.movies.value.removeAt(index)
//                                        _state.value.movies.value.add(index, test9)
//                                        _state.value.movies.value[index].random_delay = random.toLong()
//                                        _state.value.movies.value = result.data.results

                                    withContext(Dispatchers.Main) {
                                        _state.value.movies[index] =
                                            _state.value.movies[index].copy(random_delay = random.toLong())
                                    }

//                                        _state.update {
//                                            it.copy(movies = result.data.results)
//                                        }
//                                        _state.value.movies.value = mutableListOf()
//                                        _state.value.movies.value = result.data.results
//                                    }
                                    // downloadImage(movie)
                                }
                            )
                        }
                        listFetchImages.awaitAll()        //
//                    _state.value.movies.value = mutableListOf() //  result.data.results
//////
//                    _state.value.movies.value = result.data.results// newList // result.data.results
//                    _state.update { it.copy(movies = result.data.results) }

                    }
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

data class CounterState(val count: Int = 0,
//                        val movies: List<Movie> = listOf(),
//                        var movies: MutableState<MutableList<Movie>> = mutableStateOf(mutableListOf()),
                        var movies: SnapshotStateList<Movie> = mutableStateListOf(),
//
                        val loading: Boolean = false, val error: String = "")

//sealed class CounterEffect : UiEffect {
//    data class ShowToastEffect(val message: String) : CounterEffect()
//}
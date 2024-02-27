package com.example.mvi_compose.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.data.movie.Movie
import com.example.mvi_compose.movies.repositories.MovieRepoImpl
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
class MovieViewModel @Inject constructor(
    private val movieRepo: MovieRepoImpl,
    private val movieDao: MovieDao
) : BaseViewModel<MovieState, MovieEvent>() {

    override fun initialState(): MovieState {
        return MovieState()
    }

    init {
        onEvent(MovieEvent.FetchAllMovies)
    }

    override fun onEvent(event: MovieEvent) {
        when (event) {
            MovieEvent.FetchAllMovies -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(loading = true) }
                    when (val result = movieRepo.getPopularMovies()) {

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
                                }
                            }

                            withContext(Dispatchers.IO) {
                                if (movieDao.fetchFavouriteMovies().isEmpty()) {
                                    result.data.results.forEach { movie ->
                                        movieDao.insertMovie(movie)
                                    }
                                }
                            }

                            val listFetchImages = mutableListOf<Deferred<Unit>>()
                            result.data.results.forEachIndexed { index, movie ->

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

                                        withContext(Dispatchers.Main) {
                                            _state.value.movies[index] =
                                                _state.value.movies[index].copy(random_delay = random.toLong())
                                        }
                                    }
                                )
                            }
                            listFetchImages.awaitAll()        //
                            sendUiEvent(UiEffect.ShowToast("Fetched all movies"))
                        }
                    }
                }
            }
        }
    }
}

sealed class MovieEvent {
    object FetchAllMovies: MovieEvent()
}

data class MovieState(
    val count: Int = 0,

    var movies: SnapshotStateList<Movie> = mutableStateListOf(),

    val loading: Boolean = false, val error: String = ""
)
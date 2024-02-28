package com.example.mvi_compose.ui.movies

import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.data.movie.Trailer
import com.example.mvi_compose.movies.repositories.MovieRepo
import com.example.mvi_compose.movies.network.data.movie.Movie
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MovieDetailsState, MovieDetailsEvents>() {


    private var movieId: Long = 0L

    init {
        movieId = savedStateHandle.get<Long>("movieId") ?: 0L
        onEvent(MovieDetailsEvents.FetchTrailers)
        onEvent(MovieDetailsEvents.GetLikeState)
    }

    override fun initialState(): MovieDetailsState {
        return MovieDetailsState()
    }

    override fun onEvent(event: MovieDetailsEvents) {
        when (event) {
            is MovieDetailsEvents.FetchTrailers -> {
                fetchMovieTrailers()
            }

            is MovieDetailsEvents.GetLikeState -> {
                getLikeState()
            }

            is MovieDetailsEvents.UpdateLikeState -> {
                updateLikeStatus()
            }
        }
    }

    private fun fetchMovieTrailers() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                delay(2000)
                val movie = repository.getMovieById(movieId = movieId)

                Log.d("MOVIE_ID", "Movie id is 44: ${movieId.toInt()}")
                when (val result = repository.fetchMovieTrailers(movieId.toInt())) {

                    is NetworkResult.Error -> {

                    }

                    is NetworkResult.Exception -> {

                    }

                    is NetworkResult.Success -> {

                        Log.d("MOVIE_ID", "Movie id is 55: ${result}")
                        Log.d("MOVIE_ID", "Movie id is 66: ${result.data.results}")
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    trailerExternalIntent = null,
                                    trailers = result.data.results,
                                    movie = movie
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d("MOVIE_ID", "Movie id is 101: ${e.localizedMessage}")
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    private fun updateLikeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val movieData = repository.getMovieById(movieId)
            val newLikeState = if( movieData.isLiked == false ) true else false
            Log.d("LIKE_STATUS", "new like variable status is 1:  $newLikeState")
            repository.changeLikeState(movieData.id, newLikeState)
            val movieData2 = repository.getMovieById(movieId)
            Log.d("LIKE_STATUS", "new like variable status is 2:  ${movieData2.isLiked}")
            withContext(Dispatchers.Main) {
                _state.update { it.copy(isLiked = newLikeState) }
            }
        }
    }

    private fun getLikeState() {
        viewModelScope.launch(Dispatchers.IO) {
            val movieData = repository.getMovieById(movieId)
            withContext(Dispatchers.Main) {
                _state.update { it.copy(isLiked = movieData.isLiked == true) }
            }
        }
    }

}


sealed class MovieDetailsEvents {
    object FetchTrailers : MovieDetailsEvents()
    object GetLikeState : MovieDetailsEvents()
    object UpdateLikeState : MovieDetailsEvents()
}

data class MovieDetailsState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val trailers: List<Trailer>? = null,
    val isLiked: Boolean = false,
    val trailerExternalIntent: Intent? = null,
    val errorMessage: String? = null
)
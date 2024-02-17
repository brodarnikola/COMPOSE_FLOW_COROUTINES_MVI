package com.example.mvi_compose.ui

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.details.Trailer
import com.example.mvi_compose.movies.movies_list.IMovieRepo
import com.example.mvi_compose.movies.movies_list.Movie
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.utils.AppConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
class MovieDetailsViewModel @Inject constructor(
    private val repository: IMovieRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MovieDetailsState, MovieDetailsEvents>() {


    private var movieId: Long = 0L // checkNotNull(savedStateHandle["id"])

    init {
         movieId = savedStateHandle.get<Long>("movieId") ?: 0L
         onEvent(MovieDetailsEvents.FetchTrailers)
    }

    override fun initialState(): MovieDetailsState {
        return MovieDetailsState()
    }

    override fun onEvent(event: MovieDetailsEvents) {
        when(event) {
            is MovieDetailsEvents.FetchTrailers -> {
//                _state.update {
//                    it.copy(count = it.count + 1)
//                }
                sendUiEvent(UiEffect.ShowToast(message = "Incremented by one"))
                fetchMovieTrailers()
            }
            is MovieDetailsEvents.GetLikeState -> {
//                _state.update {
//                    it.copy(count = it.count - 1)
//                }
                sendUiEvent( UiEffect.ShowToast(message = "Decremented by one"))
                getLikeState()
            }
            is MovieDetailsEvents.UpdateLikeState -> {
//                _state.update {
//                    it.copy(count = it.count - 1)
//                }
                sendUiEvent( UiEffect.ShowToast(message = "Decremented by one"))
                updateLikeStatus()
            }
        }
    }


//    lateinit var movie: Movie
//
//    override fun getInitialState(): DetailsState = DetailsState(movie = movie)

//    override fun processIntents(intent: DetailsIntent) {
//        when (intent) {
//            is DetailsIntent.FetchTrailers -> fetchMovieTrailers()
//            is DetailsIntent.GetLikeState -> getLikeState()
//            is DetailsIntent.UpdateLikeState -> updateLikeStatus()
//        }
//    }

    private fun fetchMovieTrailers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }

                Log.d("MOVIE_ID", "Movie id is 44: ${movieId.toInt()}")
                when(val result = repository.fetchMovieTrailers(movieId.toInt())) {

                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Exception -> {

                    }
                    is NetworkResult.Success -> {

                        Log.d("MOVIE_ID", "Movie id is 55: ${result}")
                        Log.d("MOVIE_ID", "Movie id is 66: ${result.data.results}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                trailerExternalIntent = null,
                                trailers = result.data.results
                            )
                        }
                    }
                }


//                val results = repository.fetchMovieTrailers(movieId) //  .body()?.results
//                _state.update {
//                    it.copy(
//                        isLoading = false,
//                        trailerExternalIntent = null,
//                        trailers = results
//                    )
//                }
//                updateState { oldState ->
//                    oldState.copy(
//                        isLoading = false,
//                        trailerExternalIntent = null,
//                        trailers = results
//                    )
//                }
//                _state.update {
//                    it.copy(isLoading = false)
//                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message )
                }
            }
        }
    }

    private fun updateLikeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
//            val newLikeState = repository.isMovieLiked(movie.id).not()
//            repository.changeLikeState(movie, newLikeState)
//            withContext(Dispatchers.Main) {
////                updateState { it.copy(trailerExternalIntent = null, isLiked = newLikeState) }
//            }
        }
    }

    private fun getLikeState() {
        viewModelScope.launch(Dispatchers.IO) {
//            val likeState = repository.isMovieLiked(movie.id)
//            withContext(Dispatchers.Main) {
////                updateState { it.copy(isLiked = likeState) }
//            }
        }
    }

}


sealed class MovieDetailsEvents {
    object FetchTrailers : MovieDetailsEvents()
    object GetLikeState : MovieDetailsEvents()
    object UpdateLikeState : MovieDetailsEvents()
}

data class MovieDetailsState(
//    val count: Int = 0,
////                        val movies: List<Movie> = listOf(),
////                        var movies: MutableState<MutableList<Movie>> = mutableStateOf(mutableListOf()),
//    var movies: SnapshotStateList<Movie> = mutableStateListOf(),
//    val loading: Boolean = false,
//    val error: String = "",

    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val trailers: List<Trailer>? = null,
    val isLiked: Boolean = false,
    val trailerExternalIntent : Intent? = null,
    val errorMessage: String? = null
)
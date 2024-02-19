package com.example.mvi_compose.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.data.Trailer
import com.example.mvi_compose.movies.repositories.MovieRepo
import com.example.mvi_compose.movies.network.data.Movie
import com.example.mvi_compose.movies.repositories.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GithubLocationViewModel @Inject constructor(
    private val locationRepo: LocationRepo
) : BaseViewModel<GithubLocationState, GithubLocationEvents>() {

    init {
        onEvent(GithubLocationEvents.FetchTrailers)
        onEvent(GithubLocationEvents.GetLikeState)
    }

    override fun initialState(): GithubLocationState {
        return GithubLocationState()
    }

    override fun onEvent(event: GithubLocationEvents) {
        when (event) {
            is GithubLocationEvents.FetchTrailers -> {
//                _state.update {
//                    it.copy(count = it.count + 1)
//                }
                sendUiEvent(UiEffect.ShowToast(message = "Incremented by one"))
                fetchMovieTrailers()
            }

            is GithubLocationEvents.GetLikeState -> {
//                _state.update {
//                    it.copy(count = it.count - 1)
//                }
                sendUiEvent(UiEffect.ShowToast(message = "Decremented by one"))
//                getLikeState()
            }

            is GithubLocationEvents.UpdateLikeState -> {
//                _state.update {
//                    it.copy(count = it.count - 1)
//                }
                sendUiEvent(UiEffect.ShowToast(message = "Decremented by one"))
//                updateLikeStatus()
            }

            GithubLocationEvents.ShowLocationPermissionRequiredDialog -> TODO()
        }
    }

    private fun fetchMovieTrailers() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    it.copy(isLoading = true)
                }
                delay(2000)

            } catch (e: Exception) {
                Log.d("MOVIE_ID", "Movie id is 101: ${e.localizedMessage}")
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }

        }
    }


    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            sendUiEvent(event = GithubLocationEvents.ShowLocationPermissionRequiredDialog)
        }
    }

}


sealed class GithubLocationEvents : UiEffect {

    object ShowLocationPermissionRequiredDialog: GithubLocationEvents()
    object FetchTrailers : GithubLocationEvents()
    object GetLikeState : GithubLocationEvents()
    object UpdateLikeState : GithubLocationEvents()
}

data class GithubLocationState(

    val location: Pair<String, String> = Pair("", ""),
    val country: String = "",

    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val trailers: List<Trailer>? = null,
    val isLiked: Boolean = false,
    val trailerExternalIntent: Intent? = null,
    val errorMessage: String? = null
)
package com.example.mvi_compose.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.data.Trailer
import com.example.mvi_compose.movies.network.data.Movie
import com.example.mvi_compose.movies.repositories.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GithubLocationViewModel @Inject constructor(
    private val locationRepo: LocationRepo
) : BaseViewModel<GithubLocationState, GithubLocationEvents>() {

    var job: Job? = null
    init {
//        onEvent(GithubLocationEvents.GetUserPositionAndCountry)
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

//            GithubLocationEvents.ShowLocationPermissionRequiredDialog -> TODO()

            GithubLocationEvents.GetUserPositionAndCountry -> {
                job?.cancel()
                job = viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }
                    delay(1500)
                    val position = locationRepo.getLatitudeLongitude()
                    val countryCode = getCountryCode(position)
                    Log.d("LOCATION", "location is 1: ${position}")
                    Log.d("LOCATION", "country is 2 ${countryCode}")
                    if (position != null && countryCode != null) {
                        _state.update { it.copy(location = position, country = countryCode, isLoading = false) }
                    } else {
//                        _state.update {
//                            GithubLocationState(  )
//                        }
                    }
                    job?.cancel()
                }
            }

            is GithubLocationEvents.OnLocationPermissionGranted -> {
                onEvent(GithubLocationEvents.GetUserPositionAndCountry)
//                onContactsPermissionGranted(event.context)
            }
        }
    }

//    private fun onContactsPermissionGranted(context: Context) {
//        onEvent(GithubLocationEvents.GetUserPositionAndCountry)
////        if (isOnboardingFlow) {
////            if (sharedPref.getIsContactsInitiallySynced()) {
//                sendUiEvent(ContactsPermissionScreenUiEvent.NavigateToConnectThirdPartyAppsScreen)
////            } else {
//                // start full sync
//                _state.update { it.copy(fullSyncLoading = true) }
//                handleFullSyncWork(context)
////            }
////        } else {
//            sendUiEvent(ContactsPermissionScreenUiEvent.NavigateBack)
////        }
//    }

    private suspend fun getCountryCode(position: Pair<Double, Double>?): String? {
        position?.let {
            locationRepo.getCurrentCountry(position.first, position.second)?.let { country ->
                return  locationRepo.getCountryCode(countryName = country)
            }
        }
        return null
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


//    fun onPermissionResult(
//        isGranted: Boolean
//    ) {
//        if (!isGranted) {
//            sendUiEvent(event = GithubLocationEvents.ShowLocationPermissionRequiredDialog)
//        }
//    }

}


sealed class GithubLocationEvents : UiEffect {
    object OnLocationPermissionGranted: GithubLocationEvents()
    object GetUserPositionAndCountry: GithubLocationEvents()
//    object ShowLocationPermissionRequiredDialog: GithubLocationEvents()
    object FetchTrailers : GithubLocationEvents()
    object GetLikeState : GithubLocationEvents()
    object UpdateLikeState : GithubLocationEvents()
}

data class GithubLocationState(

    val location: Pair<Double, Double> = Pair(0.0, 0.0),
    val country: String = "",

    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val trailers: List<Trailer>? = null,
    val isLiked: Boolean = false,
    val trailerExternalIntent: Intent? = null,
    val errorMessage: String? = null
)
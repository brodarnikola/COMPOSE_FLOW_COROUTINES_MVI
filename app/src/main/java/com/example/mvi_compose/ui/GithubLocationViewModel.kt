package com.example.mvi_compose.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.RepositoryDetails
import com.example.mvi_compose.movies.repositories.GithubRepo
import com.example.mvi_compose.movies.repositories.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GithubLocationViewModel @Inject constructor(
    private val locationRepo: LocationRepo,
    @GithubNetwork private val githubRepo: GithubRepo
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
            GithubLocationEvents.GetUserPositionAndCountry -> {
                job?.cancel()
                job = viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(locationLoading = true) }
                    delay(1500)
                    val position = locationRepo.getLatitudeLongitude()
                    val countryCode = getCountryCode(position)
                    Log.d("LOCATION", "location is 1: ${position}")
                    Log.d("LOCATION", "country is 2 ${countryCode}")
//                    if (position != null && countryCode != null) {
                        _state.update { it.copy(location = position ?: Pair(0.0, 0.0), country = countryCode ?: "Wrong country code. Please try again", locationLoading = false) }
//                    } else {
//                        _state.update {
//                            GithubLocationState(  )
//                        }
//                    }
                    sendUiEvent(UiEffect.ShowToast(message = "As effect on this event is displaying your location and country code."))
                    job?.cancel()
                }
            }

            is GithubLocationEvents.OnLocationPermissionGranted -> {
                onEvent(GithubLocationEvents.GetUserPositionAndCountry)
            }

            is GithubLocationEvents.SearchGithub -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy( githubLoading = true) }
                    delay(1000)
                    when (val result = githubRepo.getSearchRepositories(event.searchText)) {

                        is NetworkResult.Error -> {

                        }

                        is NetworkResult.Exception -> {

                        }

                        is NetworkResult.Success -> {
                            Log.d("GITHUB", "githubResponseApi is 1: ${result.data}")
                            withContext(Dispatchers.Main) {
                                _state.update { it.copy(githubResponseApi = result.data.items, githubLoading = false) }
                                sendUiEvent(GithubLocationEffect.ShowGithubSuccessMessage(message = "Display effect based on user action. " +
                                        "User clicked on button search for github and because of that trigger effect. " +
                                        "Effect is displaying this text message"))
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getCountryCode(position: Pair<Double, Double>?): String? {
        position?.let {
            locationRepo.getCurrentCountry(position.first, position.second)?.let { country ->
                return  locationRepo.getCountryCode(countryName = country)
            }
        }
        return null
    }

}

sealed class GithubLocationEffect : UiEffect {
    data class ShowGithubSuccessMessage(val message: String) : GithubLocationEffect()
}

sealed class GithubLocationEvents : UiEffect {

    data class SearchGithub(val searchText: String = "") : GithubLocationEvents()
    object OnLocationPermissionGranted: GithubLocationEvents()
    object GetUserPositionAndCountry: GithubLocationEvents()
}

data class GithubLocationState(

    val location: Pair<Double, Double> = Pair(0.0, 0.0),
    val country: String = "",
    val locationLoading: Boolean = false,

    val githubResponseApi: List<RepositoryDetails> = listOf(),
    val githubLoading: Boolean = false,

)
package com.example.mvi_compose.ui.alerts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.RepositoryDetails
import com.example.mvi_compose.movies.repositories.GithubRepoImpl
import com.example.mvi_compose.ui.Resource
import com.example.mvi_compose.ui.SecondBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val githubRepo: GithubRepoImpl
) : SecondBaseViewModel<AlertContract.AlertState>() {

    override fun initialState(): AlertContract.AlertState {
        return AlertContract.AlertState()
    }

    var effects = Channel<AlertContract.Effect>(UNLIMITED)
        private set

    init {
        Log.d("MutableState", "_state.value is 00: ${_state.value}")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = githubRepo.getGithubRepositories("Android")) {
                is NetworkResult.Error -> {
                    Log.d("MutableState", "message is: ${result.message}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.message ?: "There is error occured, please try again"))
                }

                is NetworkResult.Exception -> {
                    Log.d("MutableState", "message is 2: ${result.e.localizedMessage}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.e.localizedMessage ?: "There is error occured, please try again"))
                }

                is NetworkResult.Success -> {
                    _state.value = Resource.Success( AlertContract.AlertState(isLoading = false, repositoryList = result.data.items) )
                    effects.send(AlertContract.Effect.DataWasLoaded(message = "Success.. Displayed github repositories"))
                }
            }
        }
    }

    /*override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.FetchAllGithubData -> {
                viewModelScope.launch(Dispatchers.IO + CoroutineName("awesome coroutine")) {
                    _state.update { it.copy(loading = true) }
                    delay(2000)
                    val execution = measureTimeMillis {
                        when (val result = githubRepo.getGithubRepositoriesSharedFlow("Android")) {

                            is NetworkResult.Error -> {
                                Log.d("shared flow", "apiError is: ${result.apiError}")
                                Log.d("shared flow", "message is: ${result.message}")
                                _state.update { it.copy(loading = false, error = result.message ?: "There is error occured, please try again") }
                            }

                            is NetworkResult.Exception -> {
                                Log.d("shared flow", "apiError is 1: ${result.e}")
                                Log.d("shared flow", "message is 2: ${result.e.localizedMessage}")
                                _state.update { it.copy(loading = false, error = result.e.localizedMessage ?: "There is error occured, please try again") }
                            }

                            is NetworkResult.Success -> {
                                _state.update { it.copy(loading = false, githubResponseApi = result.data.items) }
                                sendUiEvent(UiEffect.ShowToast("This is shared flow example.."))
                            }
                        }
                    }
                    Log.d("shared flow", "shared flow execution is: ${execution}")
                }
            }
        }
    }*/
}

class AlertContract {

    data class AlertState(
        val repositoryList: List<RepositoryDetails> = listOf(),
        val isLoading: Boolean = true,
        val error: String = ""
    )

    sealed class Effect {
        data class DataWasLoaded(val message: String) : Effect()
    }
}

sealed class SettingsEvent {

    object FetchAllGithubData: SettingsEvent()
}

data class SettingsState(
    val loading: Boolean = false,
    val error: String = "",
    val githubResponseApi: List<RepositoryDetails> = listOf(),
)
package com.example.mvi_compose.ui.alerts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.RepositoryDetails
import com.example.mvi_compose.movies.repositories.GithubRepoImpl
import com.example.mvi_compose.ui.Resource
import com.example.mvi_compose.ui.SecondBaseViewModel
import com.example.mvi_compose.ui.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//@Stable
@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val githubRepo: GithubRepoImpl
) : SecondBaseViewModel<AlertContract.AlertState>() { // ViewModel() { //  SecondBaseViewModel<AlertContract.AlertState>() {

    override fun initialState(): AlertContract.AlertState {
        return AlertContract.AlertState()
    }

//    private val _state3: MutableState<Resource<AlertContract.AlertState>> = mutableStateOf(Resource.Initial())
//    val state1: State<Resource<AlertContract.AlertState>>
//        get() = _state3

    fun awesomeFunc() {
        _state.value = Resource.Success()

        _state.value = Resource.Success(_state.value.unwrap()?.copy())
    }

    init {
        Log.d("MutableState", "_state.value is 00: ${_state.value}")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = githubRepo.getGithubRepositories("Android")) {
                is NetworkResult.Error -> {
                    Log.d("MutableState", "apiError is: ${result.apiError}")
                    Log.d("MutableState", "message is: ${result.message}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.message ?: "There is error occured, please try again"))
                }

                is NetworkResult.Exception -> {
                    Log.d("MutableState", "apiError is 1: ${result.e}")
                    Log.d("MutableState", "message is 2: ${result.e.localizedMessage}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.e.localizedMessage ?: "There is error occured, please try again"))
                }

                is NetworkResult.Success -> {
                    Log.d("MutableState", "_state.value is 1: ${_state.value}")
                    Log.d("MutableState", "unwrap is 1: ${_state.value.unwrap()}")
                    Log.d("MutableState", "repositoryList is 1: ${_state.value.unwrap()?.repositoryList}")
                    Log.d("MutableState", "result is: ${result.data}")
                    _state.value = Resource.Success( AlertContract.AlertState(isLoading = false, repositoryList = result.data.items) )// _state.value.unwrap()?.copy(isLoading = false, repositoryList = result.data.items))
                    Log.d("MutableState", "unwrap is 2: ${_state.value.unwrap()}")
                    Log.d("MutableState", "repositoryList is 2: ${_state.value.unwrap()?.repositoryList}")
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
        object DataWasLoaded : Effect()
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
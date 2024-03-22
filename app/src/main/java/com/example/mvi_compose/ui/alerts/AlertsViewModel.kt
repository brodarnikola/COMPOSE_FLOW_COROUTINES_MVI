package com.example.mvi_compose.ui.alerts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.general.network.NetworkResult
import com.example.mvi_compose.general.network.data.github.RepositoryDetails
import com.example.mvi_compose.general.repositories.GithubRepoImpl
import com.example.mvi_compose.network.network_connection_status.NetworkConnectionStatusManager
import com.example.mvi_compose.network.network_connection_status.NetworkConnectionStatusState
import com.example.mvi_compose.ui.Resource
import com.example.mvi_compose.ui.SecondBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val githubRepo: GithubRepoImpl,
    private val networkConnectionStatusManager: NetworkConnectionStatusManager
) : SecondBaseViewModel<AlertContract.AlertState, AlertContract.AlertsEvents>() {

    var effects = Channel<AlertContract.Effect>(UNLIMITED)
        private set

    private val _networkState = MutableStateFlow(NetworkConnectionStatusState())

    val networkState = _networkState.combine(
        networkConnectionStatusManager.isNetworkConnectedFlow
    ) { state, isNetworkConnected ->
        Log.d("MutableState", "isNetworkConnected: ${isNetworkConnected}")
        Log.d("MutableState", "isNetworkConnectedFlow: ${networkConnectionStatusManager.isNetworkConnectedFlow.value}")
        val networkState = state.copy(isNetworkConnected = isNetworkConnected)
        networkState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), NetworkConnectionStatusState())

    init {
        Log.d("MutableState", "_state.value is 00: ${_state.value}")
        networkConnectionStatusManager.startListenNetworkState()
        onEvent(AlertContract.AlertsEvents.initGetAllRepositories)
    }

    override fun initialState(): AlertContract.AlertState {
        return AlertContract.AlertState()
    }

    override fun onEvent(event: AlertContract.AlertsEvents) {
        when(event) {
            is AlertContract.AlertsEvents.getAgainAllRepositories -> {
                getAllRepositories(event.searchText) // searchText = iOS
            }
            AlertContract.AlertsEvents.initGetAllRepositories -> {
                getAllRepositories("android")
            }
        }
    }

    private fun getAllRepositories(searchText: String) {
        _state.value = Resource.Loading(AlertContract.AlertState(isLoading = true))
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = githubRepo.getGithubRepositories(searchText)) {
                is NetworkResult.Error -> {
                    Log.d("MutableState", "message is: ${result.message}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.message ?: "There is error occured, please try again"))
                }

                is NetworkResult.Exception -> {
                    Log.d("MutableState", "message is 2: ${result.e.localizedMessage}")
                    _state.value = Resource.Error(_state.value.unwrap()?.copy(error = result.e.localizedMessage ?: "There is error occured, please try again"))
                }

                is NetworkResult.Success -> {
                    withContext(Dispatchers.Main) {
                        _state.value = Resource.Success(
                            AlertContract.AlertState(
                                isLoading = false,
                                repositoryList = result.data.items
                            )
                        )
                        effects.send(AlertContract.Effect.DataWasLoaded(message = "Success.. Displayed github repositories"))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkConnectionStatusManager.stopListenNetworkState()
    }
}

class AlertContract {

    data class AlertState(
        val repositoryList: List<RepositoryDetails> = listOf(),
        val isLoading: Boolean = true,
        val error: String = ""
    )

    sealed class AlertsEvents {
         object initGetAllRepositories: AlertsEvents()
        data class getAgainAllRepositories(val searchText: String) : AlertsEvents()
    }

    sealed class Effect {
        data class DataWasLoaded(val message: String) : Effect()
    }
}
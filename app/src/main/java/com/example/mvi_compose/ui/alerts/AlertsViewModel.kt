package com.example.mvi_compose.ui.alerts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.general.network.NetworkResult
import com.example.mvi_compose.general.network.data.github.RepositoryDetails
import com.example.mvi_compose.general.repositories.GithubRepoImpl
import com.example.mvi_compose.ui.Resource
import com.example.mvi_compose.ui.SecondBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val githubRepo: GithubRepoImpl
) : SecondBaseViewModel<AlertContract.AlertState, AlertContract.AlertsEvents>() {

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

    var effects = Channel<AlertContract.Effect>(UNLIMITED)
        private set

    init {
        Log.d("MutableState", "_state.value is 00: ${_state.value}")
        onEvent(AlertContract.AlertsEvents.initGetAllRepositories)
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
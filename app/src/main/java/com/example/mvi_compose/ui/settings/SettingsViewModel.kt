package com.example.mvi_compose.ui.settings

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.RepositoryDetails
import com.example.mvi_compose.movies.repositories.GithubRepoImpl
import com.example.mvi_compose.ui.BaseViewModel
import com.example.mvi_compose.ui.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val githubRepo: GithubRepoImpl,
    private val sharedFlowExample: SharedFlowExample
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override fun initialState(): SettingsState {
        return SettingsState()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sharedFlowExample.githubFlow.collectIndexed { index, value ->

                Log.d("Shared flow", "Shared flow trigerred index: $index")
                onEvent(SettingsEvent.FetchAllGithubData)
            }
        }
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.FetchAllGithubData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(loading = true) }
                    delay(1000)
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
            }
        }
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
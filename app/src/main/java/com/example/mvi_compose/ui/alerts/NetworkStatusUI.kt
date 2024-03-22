package com.example.mvi_compose.network.network_connection_status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvi_compose.ui.alerts.AlertsViewModel


@Suppress("Unused")
@Composable
fun NetworkConnectionStatusManagerUi(viewModel: AlertsViewModel) {
    val state by viewModel.networkState.collectAsStateWithLifecycle()

    val text = when (state.isNetworkConnected) {
        true -> "Connected"
        false -> "Not connected"
        null -> "Loading..."
    }

    Text(text = text)

}

data class NetworkConnectionStatusState(
    val isNetworkConnected: Boolean? = null
)
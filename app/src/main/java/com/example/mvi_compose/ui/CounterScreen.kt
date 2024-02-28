package com.example.mvi_compose.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvi_compose.ui.movies.MovieEvent
import com.example.mvi_compose.ui.movies.MovieState
import com.example.mvi_compose.ui.movies.MovieViewModel

//import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CounterScreen(viewModel: MovieViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEffect.collect { event ->
            when (event) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
            }
        }
    }

    CounterView(state = state) { newUiEvent ->
        viewModel.onEvent(newUiEvent)  // Corrected method call
    }
}

@Composable
fun CounterView(state: MovieState, onEvent: (MovieEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Count: ${state.count}", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
//                onEvent(MovieEvent.IncrementEvent)
            }) {
                Text("Increment")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
//                onEvent(MovieEvent.DecrementEvent)
            }) {
                Text("Decrement")
            }
        }
    }
}
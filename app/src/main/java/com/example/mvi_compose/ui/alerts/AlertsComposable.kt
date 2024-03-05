package com.example.mvi_compose.ui.alerts

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mvi_compose.ui.Resource
import com.example.mvi_compose.ui.github_location.ScrollToTopButton
import com.example.mvi_compose.ui.movies.ErrorScreen
import com.example.mvi_compose.ui.movies.LoadingScreen
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {

    Log.d("AlertsScreen", "AlertsScreen enter")
    val alertsState = viewModel.state.value.unwrap() ?: AlertContract.AlertState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.receiveAsFlow().collect { event ->
            when (event) {
                is AlertContract.Effect.DataWasLoaded -> Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    when(viewModel.state.value) {
        is Resource.Error -> ErrorScreen(error = alertsState.error)
        is Resource.Initial -> {}
        is Resource.Loading -> LoadingScreen()
        is Resource.Success -> GithubListScreen(alertsState = alertsState)
    }
}

@Composable
fun GithubListScreen(alertsState: AlertContract.AlertState) {
    val githubList = remember { alertsState.repositoryList }
    val githubListState = rememberLazyListState()

    val isEnabledDerivedStateCase by remember { derivedStateOf { githubListState.firstVisibleItemIndex != 0 } }
    val coroutineScope = rememberCoroutineScope()

    Log.d("GITHUB", "githubResponseApi draw data is 1: ${githubList}")
    LazyColumn(
        state = githubListState,
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = githubList,
            key = { github ->
                // Return a stable, unique key for the github repository
                github.id
            }
        ) { github ->
            Column(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                androidx.compose.material.Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    text = "Language: ${github.language}"
                )
                androidx.compose.material.Text(
                    modifier = Modifier
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(),
                    text = "Description: ${github.description}",
                    maxLines = 3
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = isEnabledDerivedStateCase,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ScrollToTopButton(onClick = {
                coroutineScope.launch {
                    githubListState.animateScrollToItem(0)
                }
            })
        }
    }
}


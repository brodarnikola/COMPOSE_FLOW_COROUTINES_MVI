package com.example.mvi_compose.ui.github_location

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvi_compose.R
import com.example.mvi_compose.ui.GithubLocationEffect
import com.example.mvi_compose.ui.GithubLocationEvents
import com.example.mvi_compose.ui.GithubLocationViewModel
import com.example.mvi_compose.ui.UiEffect
import com.example.mvi_compose.ui.theme.PurpleGrey40


@Composable
fun GithubLocationScreen(
    viewModel: GithubLocationViewModel
) {

    val context = LocalContext.current
    val githubLocation = viewModel.state.collectAsStateWithLifecycle().value

    val githubSearchText = rememberSaveable { mutableStateOf("") }
    val githubSuccessMessage = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEffect.collect { event ->
            when (event) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
                is GithubLocationEffect.ShowGithubSuccessMessage -> {
                    githubSuccessMessage.value = event.message
                }
            }
        }
    }

    githubLocation.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            LocationScreen(context = context, locationState = githubLocation, viewModel = viewModel)
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(10.dp),
                    value = githubSearchText.value,
                    onValueChange = { newText ->
                        githubSearchText.value = newText
                    },
                    placeholder = {
                        Text(text = "Search github")
                    }
                )
                Button(
                    modifier = Modifier.weight(0.4f),
                    onClick = {
                        viewModel.onEvent(GithubLocationEvents.SearchGithub(githubSearchText.value))
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .height(35.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .wrapContentWidth(),
                        text = "Search"
                    )
                }
            }

            if(githubLocation.githubLoading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = PurpleGrey40)
                }
            }
            else {
                if( githubSuccessMessage.value.isNotEmpty() ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .wrapContentHeight()
                            .wrapContentWidth(),
                        color = colorResource(id = R.color.purple_700),
                        text = githubSuccessMessage.value
                    )
                }
                val githubList = remember { githubLocation.githubResponseApi }
                Log.d("GITHUB", "githubResponseApi draw data is 1: ${githubList}")
                LazyColumn(
                    state = rememberLazyListState(),
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
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .wrapContentWidth(),
                                text = "Language: ${github.language}"
                            )
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight(Alignment.CenterVertically)
                                    .wrapContentWidth(),
                                text = "Description: ${github.description}",
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }
}

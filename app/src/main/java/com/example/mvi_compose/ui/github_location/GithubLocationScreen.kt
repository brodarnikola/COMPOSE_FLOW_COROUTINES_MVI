package com.example.mvi_compose.ui.github_location

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.R
import com.example.mvi_compose.movies.network.data.Trailer
import com.example.mvi_compose.ui.GithubLocationEvents
import com.example.mvi_compose.ui.GithubLocationViewModel
import com.example.mvi_compose.ui.MovieDetailsState
import com.example.mvi_compose.ui.UiEffect
import com.example.mvi_compose.ui.dialogs.ConfirmOrCancelDialog
import com.example.mvi_compose.ui.movies.LoadingScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GithubLocationScreen(
    viewModel: GithubLocationViewModel
) {

    val showSettingsDialog = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val githubLocation = viewModel.state.collectAsStateWithLifecycle().value

    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION) {
            if (it) {
                viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
            } else {
                showSettingsDialog.value = true
            }
        }

    LaunchedEffect(key1 = Unit) {

        viewModel.uiEffect.collect { event ->
            when (event) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }

//                is GithubLocationEvents.ShowLocationPermissionRequiredDialog -> {
////                    isDisplayedPermissionDialog.value = true
//                }
            }
        }
    }


    if (showSettingsDialog.value) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            if (locationPermissionState.status.isGranted) {
                showSettingsDialog.value = false
                viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
            }
        }
        ConfirmOrCancelDialog(
            titleText = "Permission required",
            descriptionText = "This app requires access to your location in order to display your position",
            cancelText = "",
            confirmText = "Grant permission",
            onConfirmOrCancel = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                launcher.launch(intent)
                locationPermissionState.launchPermissionRequest()
                showSettingsDialog.value = false
            }
        )
    }

    githubLocation.let {

        if (githubLocation.country.isNotEmpty() && githubLocation.location.first != 0.0) {
            Log.d("LOCATION", "Country is 2: ${githubLocation.country}")
            Log.d("LOCATION", "location is 2: ${githubLocation.location}")
            ConstraintLayout(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                val countryCode = createRef()
                val position = createRef()

                Text(
                    modifier = Modifier
                        .constrainAs(countryCode) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = "Country: ${githubLocation.country}"
                )
                Text(
                    modifier = Modifier
                        .constrainAs(position) {
                            start.linkTo(parent.start)
                            top.linkTo(countryCode.bottom)
                        }
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = "Latitude: ${githubLocation.location.first},\nLongitude: ${githubLocation.location.second}"
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .height(35.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .fillMaxWidth(.9f),
                text = "Display your country and position"
            )
            if (githubLocation.isLoading) LoadingScreen()
            else {
                Button(onClick = {
                    Log.d(
                        "LOCATION",
                        "locationPermissionState status  isGranted: ${locationPermissionState.status.isGranted}"
                    )
                    if (locationPermissionState.status.isGranted) {
                        showSettingsDialog.value = false
                        viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text(
                        modifier = Modifier
                            .height(35.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .fillMaxWidth(.9f),
                        text = "Display"
                    )
                }
            }
        }

        /*if (githubLocation.isLoading) LoadingScreen()
        else if (githubLocation.country.isNotEmpty() && githubLocation.location.first != 0.0) {
            Log.d("LOCATION", "Country is 2: ${githubLocation.country}")
            Log.d("LOCATION", "location is 2: ${githubLocation.location}")
            ConstraintLayout(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                val countryCode = createRef()
                val position = createRef()

                Text(
                    modifier = Modifier
                        .constrainAs(countryCode) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = "Country: ${githubLocation.country}"
                )
                Text(
                    modifier = Modifier
                        .constrainAs(position) {
                            start.linkTo(parent.start)
                            top.linkTo(countryCode.bottom)
                        }
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = "Latitude: ${githubLocation.location.first},\nLongitude: ${githubLocation.location.second}"
                )
            }
        }*/
    }
}

@Composable
fun MovieDetailsDataScreen(
    detailsState: MovieDetailsState,
    navigateUp: () -> Unit,
    onFavClicked: () -> Unit,
    onTrailerClick: (trailerKey: String) -> Unit,
) {

    ConstraintLayout(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        val arrowBack = createRef()
        val logo = createRef()
        val title = createRef()
        val release = createRef()
        val votes = createRef()
        val rate = createRef()
        val likeIcon = createRef()
        val plot = createRef()
        val trailersList = createRef()

        val backgroundColor = colorResource(id = R.color.purple_200)
        Image(
            painter = painterResource(id = R.drawable.ic_m3_back_arrow),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .constrainAs(arrowBack) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .drawBehind {
                    drawCircle(backgroundColor)
                }
                .size(40.dp)
                .clickable {
                    navigateUp()
                }
        )

        Image(
            painter = rememberAsyncImagePainter("${BuildConfig.IMAGE_URL}${detailsState.movie?.poster_path}"),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(arrowBack.bottom, margin = 10.dp)
                }
                .width(160.dp)
                .height(135.dp)
                .padding(end = 10.dp)
        )


    }
}

@Composable
fun TrailerList(trailers: List<Trailer>, onTrailerClick: (String) -> Unit) {

    val finalTrailersList = remember { trailers }
    LazyColumn {
        items(
            items = finalTrailersList,
            key = { trailer ->
                trailer.id
            }
        ) { trailer ->
            Row(Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onTrailerClick(trailer.key)
                },
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = trailer.name
                )

                Image(
                    modifier = Modifier
                        .padding(2.dp),
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    colorFilter = ColorFilter.tint(Color.Red),
                    contentDescription = "play icon"
                )
            }
        }
    }
}

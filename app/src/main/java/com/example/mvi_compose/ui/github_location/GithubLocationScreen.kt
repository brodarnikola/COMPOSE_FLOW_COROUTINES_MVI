package com.example.mvi_compose.ui.github_location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
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
import com.example.mvi_compose.ui.dialogs.ConfirmOrCancelDialog
import com.example.mvi_compose.ui.theme.PurpleGrey40
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GithubLocationScreen(
    viewModel: GithubLocationViewModel
) {

    val showSettingsLocationDialog = rememberSaveable { mutableStateOf(false) }
    val showEnableLocationGPSDialog = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val githubLocation = viewModel.state.collectAsStateWithLifecycle().value

    val githubSearchText = rememberSaveable { mutableStateOf("") }
    val githubSuccessMessage = rememberSaveable { mutableStateOf("") }

    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION) {
            Log.d("LOCATION_TURNED_ON", "is permissions granted 1: ${it}")
            if (it) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showEnableLocationGPSDialog.value = true
                }
                else {
                    viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
                }
            } else {
                showSettingsLocationDialog.value = true
            }
        }

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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        Log.d("LOCATION_TURNED_ON", "is permissions granted 2: ${locationPermissionState.status.isGranted}")
        if (locationPermissionState.status.isGranted) {
            showSettingsLocationDialog.value = false

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS is not enabled, prompt the user to enable it
                // You can show a dialog here to prompt the user to enable GPS
                Log.d("LOCATION_TURNED_ON", "is location turned  on.. false")
                showEnableLocationGPSDialog.value = true
            }
            else {
                viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
            }
        }
        else {
            showSettingsLocationDialog.value = false
        }
    }

    if (showSettingsLocationDialog.value) {
        ConfirmOrCancelDialog(
            titleText = "Permission required",
            descriptionText = "This app requires access to your location in order to display your position",
            cancelText = "",
            confirmText = "Grant permission",
            onConfirmOrCancel = {
                showSettingsLocationDialog.value = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                locationPermissionLauncher.launch(intent)
                locationPermissionState.launchPermissionRequest()
            }
        )
    }

    if (showEnableLocationGPSDialog.value) {
        val locationEnabledResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Log.d("LOCATION_TURNED_ON", "locationManager status is.. ${locationManager.isLocationEnabled}")
                Log.d("LOCATION_TURNED_ON", "is true.. ${locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)}")
            }
            if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
                showEnableLocationGPSDialog.value = false
            }
        }
        AlertDialog(
            onDismissRequest = {
                showEnableLocationGPSDialog.value = false
            },
            title = { Text(text = "Location Services Disabled") },
            text = { Text(text = "Please enable location services in order to use this app.") },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        locationEnabledResultLauncher.launch(intent)
                         //ContextCompat.startActivity(context, intent, null)
                    }
                ) {
                    Text(text = "Open Settings")
                }
            },
            dismissButton = {
                Button(
                    onClick =  {
                        showEnableLocationGPSDialog.value = false
                    }
                ) {
                    Text(text = "Dismiss")
                }
            }
        )
    }

    githubLocation.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (githubLocation.country.isEmpty() && githubLocation.location.first == 0.0) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .width(200.dp)
                            .wrapContentHeight(),
                        text = "Display your country code and position"
                    )
                    if (githubLocation.locationLoading) {
                        CircularProgressIndicator(color = PurpleGrey40)
                    } else {
                        Button(onClick = {
                            Log.d(
                                "LOCATION",
                                "locationPermissionState status  isGranted: ${locationPermissionState.status.isGranted}"
                            )
                            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            when {
                                locationPermissionState.status.isGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                                    showSettingsLocationDialog.value = false
                                    viewModel.onEvent(GithubLocationEvents.OnLocationPermissionGranted)
                                }
                                !locationPermissionState.status.isGranted -> {
                                    locationPermissionState.launchPermissionRequest()
                                }
                                !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                                    showEnableLocationGPSDialog.value = true
                                }
                            }
                        }) {
                            Text(
                                modifier = Modifier
                                    .height(35.dp)
                                    .wrapContentHeight(Alignment.CenterVertically)
                                    .wrapContentWidth(),
                                text = "Display"
                            )
                        }
                    }
                }
            } else {
                Text(
                    modifier = Modifier
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(.9f),
                    text = "Country code: ${githubLocation.country}"
                )
                Text(
                    modifier = Modifier
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 20.dp, )
                        .fillMaxWidth(.9f),
                    text = "Latitude: ${githubLocation.location.first},\nLongitude: ${githubLocation.location.second}"
                )
            }
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

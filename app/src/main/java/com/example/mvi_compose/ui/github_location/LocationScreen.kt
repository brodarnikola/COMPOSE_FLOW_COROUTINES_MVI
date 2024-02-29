package com.example.mvi_compose.ui.github_location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvi_compose.ui.dialogs.ConfirmOrCancelDialog
import com.example.mvi_compose.ui.theme.PurpleGrey40
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(
    context: Context,
    locationState: GithubLocationState,
    viewModel: GithubLocationViewModel
) {

    val showSettingsLocationDialog = rememberSaveable { mutableStateOf(false) }
    val showEnableLocationGPSDialog = rememberSaveable { mutableStateOf(false) }

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
        val locationEnabledResultLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { _ ->
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Log.d("LOCATION_TURNED_ON", "locationManager status is.. ${locationManager.isLocationEnabled}")
                Log.d("LOCATION_TURNED_ON", "is true.. ${locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER)}")
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

    if (locationState.country.isEmpty() && locationState.location.first == 0.0) {
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
            if (locationState.locationLoading) {
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
            text = "Country code: ${locationState.country}"
        )
        Text(
            modifier = Modifier
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(horizontal = 20.dp, )
                .fillMaxWidth(.9f),
            text = "Latitude: ${locationState.location.first},\nLongitude: ${locationState.location.second}"
        )
    }

}
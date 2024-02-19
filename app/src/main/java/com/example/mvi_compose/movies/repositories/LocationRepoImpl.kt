package com.example.mvi_compose.movies.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

import com.google.android.gms.location.FusedLocationProviderClient

class LocationRepoImpl
@Inject
constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
): LocationRepo {

    override fun getCountryCode(countryName: String): String? = Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    override suspend fun getCurrentCountry(latitude: Double, longitutde: Double): String? = suspendCoroutine { continuation: Continuation<String?> ->
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude,
                    longitutde,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                continuation.resume(address.countryName)
                            } else {
                                throw Exception()
                            }
                        }
                        override fun onError(errorMessage: String?) {
                            super.onError(errorMessage)
                            throw Exception(errorMessage)
                        }
                    }
                )
            } else {
                val addressList = geocoder.getFromLocation(latitude, longitutde, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    continuation.resume(address.countryName)
                } else {
                    throw Exception()
                }
            }
        } catch (e: Exception) {
            // handle exceptions
            continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLatitudeLongitude(): Pair<Double, Double>? = suspendCoroutine { continuation: Continuation<Pair<Double, Double>?> ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Pair(location.latitude, location.longitude))
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}
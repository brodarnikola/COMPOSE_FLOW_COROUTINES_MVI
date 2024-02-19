package com.example.mvi_compose.movies.repositories

interface LocationRepo {
    fun getCountryCode(countryName: String): String?
    suspend fun getCurrentCountry(latitude: Double, longitutde: Double): String?
    suspend fun getLatitudeLongitude(): Pair<Double, Double>?
}
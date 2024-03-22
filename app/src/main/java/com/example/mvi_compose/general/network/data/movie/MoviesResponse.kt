package com.example.mvi_compose.general.network.data.movie

import androidx.compose.runtime.Immutable

@Immutable
data class MoviesResponse(
    val page: Int?,
    val total_results: Int?,
    val total_pages: Int,
    val results: MutableList<Movie>)
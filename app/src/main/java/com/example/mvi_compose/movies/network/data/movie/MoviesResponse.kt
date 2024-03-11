package com.example.mvi_compose.movies.network.data.movie

import android.os.Parcelable
import androidx.compose.runtime.Immutable

@Immutable
data class MoviesResponse(
    val page: Int?,
    val total_results: Int?,
    val total_pages: Int,
    val results: MutableList<Movie>)
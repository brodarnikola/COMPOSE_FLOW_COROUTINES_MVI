package com.example.mvi_compose.movies.network.data.movie

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.example.mvi_compose.movies.network.data.movie.Movie
import kotlinx.parcelize.Parcelize
@Immutable
@Parcelize
data class MoviesResponse(
    val page: Int?,
    val total_results: Int?,
    val total_pages: Int,
    val results: MutableList<Movie>) : Parcelable
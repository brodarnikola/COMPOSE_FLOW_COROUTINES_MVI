package com.example.mvi_compose.movies.network.data

import android.os.Parcelable
import com.example.mvi_compose.movies.network.data.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviesResponse(
    var page: Int?,
    var total_results: Int?,
    var total_pages: Int,
    var results: MutableList<Movie>) : Parcelable
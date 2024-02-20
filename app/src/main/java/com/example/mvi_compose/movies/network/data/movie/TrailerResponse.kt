package com.example.mvi_compose.movies.network.data.movie

import android.os.Parcelable
import com.example.mvi_compose.movies.network.data.movie.Trailer
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrailerResponse(
    var id: Int,
    var results: List<Trailer>
) : Parcelable
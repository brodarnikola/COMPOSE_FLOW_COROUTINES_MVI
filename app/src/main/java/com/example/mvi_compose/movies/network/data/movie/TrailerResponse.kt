package com.example.mvi_compose.movies.network.data.movie

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.example.mvi_compose.movies.network.data.movie.Trailer
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class TrailerResponse(
    val id: Int,
    val results: List<Trailer>
) : Parcelable
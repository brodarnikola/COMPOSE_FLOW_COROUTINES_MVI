package com.example.mvi_compose.movies.network.data

import android.os.Parcelable
import com.example.mvi_compose.movies.network.data.Trailer
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrailerResponse(
    var id: Int,
    var results: List<Trailer>
) : Parcelable
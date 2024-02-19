package com.example.mvi_compose.movies.details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrailerResponse(
    var id: Int,
    var results: List<Trailer>
) : Parcelable
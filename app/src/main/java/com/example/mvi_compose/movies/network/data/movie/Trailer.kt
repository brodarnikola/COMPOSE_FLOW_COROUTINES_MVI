package com.example.mvi_compose.movies.network.data.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trailer(
    val id: String,
    val iso_639_1: String? = null,
    val iso_3166_1: String? = null,
    val key: String,
    val name: String,
    val site: String? = null,
    var size: Int? = null,
    val type: String? = null
) : Parcelable
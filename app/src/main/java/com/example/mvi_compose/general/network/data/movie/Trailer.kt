package com.example.mvi_compose.general.network.data.movie

import androidx.compose.runtime.Immutable

@Immutable
data class Trailer(
    val id: String,
    val iso_639_1: String? = null,
    val iso_3166_1: String? = null,
    val key: String,
    val name: String,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null
)
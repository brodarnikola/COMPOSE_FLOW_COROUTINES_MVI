package com.example.mvi_compose.movies.network.data.movie

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class TrailerResponse(
    val id: Int,
    val results: List<Trailer> = listOf()
)


@Immutable
data class TrailerResponseMapper(
    val results: ImmutableList<Trailer> = persistentListOf()
)
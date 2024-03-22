package com.example.mvi_compose.general.network.data.movie

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "general")
data class Movie(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "vote_count")
    val vote_count: Int,
    @ColumnInfo(name = "random_delay")
    val random_delay: Long,
    @ColumnInfo(name = "video")
    val video: Boolean,
    @ColumnInfo(name = "vote_average")
    val vote_average: Double,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "popularity")
    val popularity: Double,
    @ColumnInfo(name = "poster_path")
    val poster_path: String?,
    @ColumnInfo(name = "original_language")
    val original_language: String?,
    @ColumnInfo(name = "original_title")
    val original_title: String?,
    @ColumnInfo(name = "backdrop_path")
    val backdrop_path: String? = "null",
    @ColumnInfo(name = "adult")
    val adult: Boolean = false,
    @ColumnInfo(name = "isLiked")
    val isLiked: Boolean? = false,
    @ColumnInfo(name = "overview")
    val overview: String?,
    @ColumnInfo(name = "release_date")
    val release_date: String?
)
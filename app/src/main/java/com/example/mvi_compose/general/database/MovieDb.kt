package com.example.mvi_compose.general.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton
import com.example.mvi_compose.general.network.data.movie.Movie

@Singleton
@Database(entities = [(Movie::class)], version = 2, exportSchema = false)
abstract class MovieDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
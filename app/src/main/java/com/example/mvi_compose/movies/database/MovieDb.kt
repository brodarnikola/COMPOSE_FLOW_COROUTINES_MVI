package com.example.mvi_compose.movies.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton
import com.example.mvi_compose.movies.movies_list.Movie

@Singleton
@Database(entities = [(Movie::class)], version = 1, exportSchema = false)
abstract class MovieDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
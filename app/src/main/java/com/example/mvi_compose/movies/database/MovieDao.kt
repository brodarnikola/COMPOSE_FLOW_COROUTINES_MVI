package com.example.mvi_compose.movies.utils

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_compose.movies.network.data.movie.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long) : Movie

    @Query("UPDATE movies set isLiked = :isLiked WHERE id = :movieId")
    suspend fun updateLikeStatus(movieId: Int, isLiked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Query("SELECT id FROM movies")
    suspend fun fetchFavouriteMovies(): List<Int?>

    @Delete()
    suspend fun removeMovie(movie: Movie)

}
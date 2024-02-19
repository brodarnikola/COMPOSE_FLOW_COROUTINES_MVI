package com.example.mvi_compose.movies.utils

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_compose.movies.movies_list.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovieById(movieId: Long) : Movie

    @Query("UPDATE movies set isLiked = :isLiked WHERE id = :movieId")
    suspend fun updateLikeStatus(movieId: Int, isLiked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie)

    @Query("SELECT id FROM movies")
    fun fetchFavouriteMovies(): List<Int?>

    @Delete()
    fun removeMovie(movie: Movie)

}
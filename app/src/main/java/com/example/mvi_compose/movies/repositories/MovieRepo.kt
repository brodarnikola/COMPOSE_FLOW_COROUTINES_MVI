package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.network.data.movie.TrailerResponse
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.movie.Movie
import com.example.mvi_compose.movies.network.data.movie.MoviesResponse

interface MovieRepo {

    suspend fun getMovieById(movieId: Long): Movie
    suspend fun getPopularMovies(): NetworkResult<MoviesResponse>
    suspend fun fetchMovieTrailers(movieId: Int): NetworkResult<TrailerResponse>
//    fun isMovieLiked(id: Int): Boolean
    suspend fun changeLikeState(movieId: Int, newLikeState: Boolean)
}
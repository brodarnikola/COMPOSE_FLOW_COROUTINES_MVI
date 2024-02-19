package com.example.mvi_compose.movies.movies_list

import com.example.mvi_compose.movies.details.TrailerResponse
import com.example.mvi_compose.movies.network.NetworkResult
import retrofit2.Response

interface IMovieRepo {

    suspend fun getMovieById(movieId: Long): Movie
    suspend fun getPopularMovies(): NetworkResult<MoviesResponse>
    suspend fun fetchMovieTrailers(movieId: Int): NetworkResult<TrailerResponse>
//    fun isMovieLiked(id: Int): Boolean
    suspend fun changeLikeState(movieId: Int, newLikeState: Boolean)
}
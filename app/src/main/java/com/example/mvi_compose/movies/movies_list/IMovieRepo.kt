package com.example.mvi_compose.movies.movies_list

import com.example.mvi_compose.movies.details.TrailerResponse
import com.example.mvi_compose.movies.network.NetworkResult
import retrofit2.Response

interface IMovieRepo {
    suspend fun getPopularMovies(): NetworkResult<MoviesResponse>
    suspend fun fetchMovieTrailers(movieId: Int): Response<TrailerResponse>
    fun isMovieLiked(id: Int): Boolean
    fun changeLikeState(movie: Movie, newLikeState: Boolean)
}
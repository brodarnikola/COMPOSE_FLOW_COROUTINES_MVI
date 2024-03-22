package com.example.mvi_compose.general.repositories

import com.example.mvi_compose.general.network.data.movie.TrailerResponse
import com.example.mvi_compose.general.network.NetworkResult
import com.example.mvi_compose.general.network.data.movie.Movie
import com.example.mvi_compose.general.network.data.movie.MoviesResponse

interface MovieRepo {

    suspend fun getMovieById(movieId: Long): Movie
    suspend fun getPopularMovies(): NetworkResult<MoviesResponse>
    suspend fun fetchMovieTrailers(movieId: Int): NetworkResult<TrailerResponse>
    suspend fun changeLikeState(movieId: Int, newLikeState: Boolean)
}
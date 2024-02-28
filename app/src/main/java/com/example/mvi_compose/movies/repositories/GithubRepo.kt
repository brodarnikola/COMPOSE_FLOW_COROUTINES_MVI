package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import com.example.mvi_compose.movies.network.data.movie.MoviesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface GithubRepo {

    suspend fun getSearchRepositories(query: String) : Flow<GithubResponseApi>

    suspend fun getGithubRepositoriesSharedFlow(query: String) : NetworkResult<GithubResponseApi>
}
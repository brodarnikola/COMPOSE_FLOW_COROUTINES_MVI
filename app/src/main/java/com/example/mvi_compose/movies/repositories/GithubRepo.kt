package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi


interface GithubRepo {

    suspend fun getSearchRepositories(query: String) : NetworkResult<GithubResponseApi>

}
package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import kotlinx.coroutines.flow.Flow


interface GithubRepo {

    suspend fun getSearchRepositories(query: String) : Flow<GithubResponseApi>

}
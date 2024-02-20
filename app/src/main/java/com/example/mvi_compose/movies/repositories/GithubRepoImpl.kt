package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import javax.inject.Inject

class GithubRepoImpl @Inject constructor(
    @GithubNetwork private val service: GithubApi
): GithubRepo {
    override suspend fun getSearchRepositoriesResultStream(query: String): GithubResponseApi {
        return service.searchGithubRepository("kotlin", 0, 20)
    }

}
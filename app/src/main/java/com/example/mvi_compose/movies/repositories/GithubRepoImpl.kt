package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GithubRepoImpl @Inject constructor(
    @GithubNetwork private val service: GithubApi
): GithubRepo {
    override suspend fun getSearchRepositories(query: String) : Flow<GithubResponseApi> {

        var counter = 0

        val latestNews: Flow<GithubResponseApi> = flow {
            while(counter < 3) {
                val latestNews = service.searchGithubRepository(query, 1, 10).body()
                val newResponse = latestNews?.items?.map {
                    val upperCase = it.copy(language = it.language?.uppercase())
                    upperCase
                }
                val finalResponse = latestNews?.copy(items = newResponse!!)
                finalResponse?.let { emit(it) } // Emits the result of the request to the flow
                delay(2000) // Suspends the coroutine for some time
                counter++
            }
        }

        return latestNews
    }
}
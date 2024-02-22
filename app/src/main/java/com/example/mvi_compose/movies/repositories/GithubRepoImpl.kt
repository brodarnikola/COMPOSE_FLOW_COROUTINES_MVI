package com.example.mvi_compose.movies.repositories

import android.util.Log
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.ApiError
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import com.example.mvi_compose.movies.utils.AppConstants
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class GithubRepoImpl @Inject constructor(
    @GithubNetwork private val service: GithubApi,
    private val moshi: Moshi
): GithubRepo {
    override suspend fun getSearchRepositories(query: String) : Flow<GithubResponseApi> {

        var counter = 0

        val latestNews: Flow<GithubResponseApi> = flow {
            while(counter < 3) {
                val latestNews = service.searchGithubRepository(query, 1, 10)
                latestNews.body()?.let { emit(it) } // Emits the result of the request to the flow
                delay(2000) // Suspends the coroutine for some time
                counter++
            }
        }

        return latestNews
    }
}
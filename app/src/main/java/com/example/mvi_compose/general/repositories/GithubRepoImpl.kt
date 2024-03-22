package com.example.mvi_compose.general.repositories

import android.util.Log
import com.example.mvi_compose.general.di.IODispatcher
import com.example.mvi_compose.general.di.github.GithubNetwork
import com.example.mvi_compose.general.network.ApiError
import com.example.mvi_compose.general.network.GithubApi
import com.example.mvi_compose.general.network.NetworkResult
import com.example.mvi_compose.general.network.data.github.GithubResponseApi
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
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
    @GithubNetwork private val moshi: Moshi,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
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

    //Shared flow example
    override suspend fun getGithubRepositoriesSharedFlow(query: String): NetworkResult<GithubResponseApi> = withContext(ioDispatcher) {
        val networkResult = handleNetworkRequest {
            Log.d("Shared flow","start fetching rest api.. this is for shared flow example")
            service.searchGithubRepositorySharedFlowExample(query, 1, 20)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d("Shared flow","done fetching api")
        }
        else if( networkResult is NetworkResult.Error) {
            Log.d("Shared flow","error fetching api . .${networkResult.message}")
        }

        return@withContext networkResult
    }

    override suspend fun getGithubRepositories(query: String): NetworkResult<GithubResponseApi> = withContext(ioDispatcher) {
        val networkResult = handleNetworkRequest {
            Log.d("MutableState","start fetching rest api.. this is mutable state example")
            service.searchGithubRepositorySharedFlowExample(query, 1, 20)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d("MutableState","mutable state done fetching api")
        }
        else if( networkResult is NetworkResult.Error) {
            Log.d("MutableState","mutable state  error fetching api . .${networkResult.message}")
        }

        return@withContext networkResult
    }

    override fun getSearchRepositorieRxJava2(query: String, page: Int, perPage: Int): Single<GithubResponseApi> {
        val repos = service.searchGithubRepositoryWithRxJava2(query, page, perPage)
        return repos
    }

    override fun getSearchRepositorieWithFlowableRxJava2(query: String): Flowable<GithubResponseApi> {
        val repositoryResult = service.searchGithubRepositoryWithFlowable(query, 1, 10)
        return repositoryResult
    }

    private suspend fun <T : Any> handleNetworkRequest(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response: Response<T> = apiCall.invoke()

            if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                var apiError: ApiError? = null
                if (errorBody != null) {
                    try {
                        val adapter = moshi.adapter(ApiError::class.java)
                        apiError = adapter.fromJson(errorBody)
                    } catch (e: Exception) {
                        Log.e("Error","handleNetworkRequest error: ${e.localizedMessage}")
                    }
                }
                Log.e("Error","handleNetworkRequest error: ${response.message()}")
                Log.e("Error","handleNetworkRequest error: $apiError")
                NetworkResult.Error(
                    code = response.code(),
                    message = response.message(),
                    apiError = apiError
                )
            }
        } catch (e: HttpException) {
            Log.e("NETWORK_HTTP_ERROR","Network request error - HttpException: ${e.localizedMessage}")
            NetworkResult.Error(
                code = e.code(),
                message = e.message(),
                apiError = null
            )
        } catch (e: IOException) {
            Log.e("NETWORK_IOEXCEPTION_ERROR","Network request error - IOException: ${e.localizedMessage}")
            NetworkResult.Exception(e)
        }
    }
}
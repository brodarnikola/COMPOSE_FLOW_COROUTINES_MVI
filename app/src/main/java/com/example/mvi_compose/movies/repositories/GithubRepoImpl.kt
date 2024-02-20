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
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class GithubRepoImpl @Inject constructor(
    @GithubNetwork private val service: GithubApi,
    private val moshi: Moshi
): GithubRepo {
    override suspend fun getSearchRepositories(query: String): NetworkResult<GithubResponseApi> = withContext(Dispatchers.IO) {
        val networkResult = handleNetworkRequest {
            Log.d(AppConstants.REST_API_CALL,"get github repositories search")
            service.searchGithubRepository(query, 1, 10)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d(AppConstants.REST_API_CALL,"success github repositories search")
        }

        return@withContext networkResult
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
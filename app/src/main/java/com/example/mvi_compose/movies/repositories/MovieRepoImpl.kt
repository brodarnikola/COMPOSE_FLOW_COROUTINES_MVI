package com.example.mvi_compose.movies.repositories

import android.util.Log
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.di.IODispatcher
import com.example.mvi_compose.movies.di.MovieNetwork
import com.example.mvi_compose.movies.network.TrailerApi
import com.example.mvi_compose.movies.network.data.movie.TrailerResponse
import com.example.mvi_compose.movies.network.ApiError
import com.example.mvi_compose.movies.network.MovieApi
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.network.data.movie.Movie
import com.example.mvi_compose.movies.network.data.movie.MoviesResponse
import com.example.mvi_compose.movies.utils.AppConstants.Companion.REST_API_CALL
import com.example.mvi_compose.movies.utils.MovieDao
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepoImpl @Inject constructor(
    private val movieDao: MovieDao,
    @MovieNetwork private val movieApi: MovieApi,
    @MovieNetwork private val trailerApi: TrailerApi,
    @MovieNetwork private val moshi: Moshi,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : MovieRepo {

    override suspend fun getMovieById(movieId: Long): Movie = withContext(ioDispatcher) {
        return@withContext movieDao.getMovieById(movieId)
    }

    override suspend fun getPopularMovies(): NetworkResult<MoviesResponse> = withContext(ioDispatcher) {

        val networkResult = handleNetworkRequest {
            Log.d(REST_API_CALL,"start popular moview")
            movieApi.getMostPopular(BuildConfig.API_KEY)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d(REST_API_CALL,"get movies success")
        }

        return@withContext networkResult
    }

    override suspend fun fetchMovieTrailers(movieId: Int) : NetworkResult<TrailerResponse> = withContext(ioDispatcher) {

        val networkResult = handleNetworkRequest {
            Log.d(REST_API_CALL,"get movie trailers")
            trailerApi.getMovieTrailer(movieId, BuildConfig.API_KEY)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d(REST_API_CALL,"get movie trailers success")
        }

        return@withContext networkResult
    }

    override suspend fun changeLikeState(movieId: Int, newLikeState: Boolean) = withContext(ioDispatcher) {
        movieDao.updateLikeStatus(movieId, newLikeState)
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
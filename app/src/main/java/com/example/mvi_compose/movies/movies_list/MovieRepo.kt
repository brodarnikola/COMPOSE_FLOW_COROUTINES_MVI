package com.example.mvi_compose.movies.movies_list

import android.util.Log
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.details.TrailerApi
import com.example.mvi_compose.movies.details.TrailerResponse
import com.example.mvi_compose.movies.network.ApiError
import com.example.mvi_compose.movies.network.NetworkResult
import com.example.mvi_compose.movies.utils.AppConstants.Companion.REST_API_CALL
import com.example.mvi_compose.movies.utils.MovieDao
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(
    private val movieDao: MovieDao,
    private val movieApi: MovieApi,
    private val trailerApi: TrailerApi,
    private val moshi: Moshi
) : IMovieRepo {

    override suspend fun getPopularMovies(): NetworkResult<MoviesResponse> = withContext(Dispatchers.IO) {

        val networkResult = handleNetworkRequest {
            Log.d(REST_API_CALL,"start popular moview")
            movieApi.getMostPopular(BuildConfig.API_KEY)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d(REST_API_CALL,"start popular moview response is")
        }

        return@withContext networkResult
//        return movieApi.getMostPopular(BuildConfig.API_KEY)
    }

    override suspend fun fetchMovieTrailers(movieId: Int) : NetworkResult<TrailerResponse> = withContext(Dispatchers.IO) {

        val networkResult = handleNetworkRequest {
            Log.d(REST_API_CALL,"get movie trailers")
            trailerApi.getMovieTrailer(movieId, BuildConfig.API_KEY)
        }

        if( networkResult is NetworkResult.Success ) {
            Log.d(REST_API_CALL,"start popular moview response is")
        }

        return@withContext networkResult

//        return trailerApi.getMovieTrailer(movieId, BuildConfig.API_KEY)
    }

    override fun isMovieLiked(id: Int): Boolean {
        return movieDao.fetchFavouriteMovies().contains(id)
    }

    override fun changeLikeState(movie: Movie, newLikeState: Boolean) {
        if (newLikeState) movieDao.insertMovie(movie)
        else movieDao.removeMovie(movie)
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
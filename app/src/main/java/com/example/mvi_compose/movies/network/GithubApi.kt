package com.example.mvi_compose.movies.network


import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton


@Singleton
interface GithubApi {

    @GET("search/repositories")
    suspend fun searchGithubRepository(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<GithubResponseApi>

    @GET("search/repositories")
    suspend fun searchGithubRepositorySharedFlowExample(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<GithubResponseApi>

}
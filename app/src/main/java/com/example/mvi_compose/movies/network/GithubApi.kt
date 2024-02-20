package com.example.mvi_compose.movies.network


import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Used to connect to the Unsplash API to fetch photos
 */
interface GithubApi {

    @GET("search/repositories")
    suspend fun searchGithubRepository(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): GithubResponseApi

    @GET("search/repositories?sort=stars&order=desc")
    suspend fun searchGithubRepositoryByProgrammingLanguage(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): GithubResponseApi

}
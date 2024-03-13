package com.example.mvi_compose.movies.network


import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import com.example.mvi_compose.ui.rxJavaExamples.Comment
import com.example.mvi_compose.ui.rxJavaExamples.Post
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
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





    // example for rxjava2
    @GET("search/repositories")
    fun searchGithubRepositoryWithRxJava2(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Single<GithubResponseApi>

    @GET("search/repositories")
    fun searchGithubRepositoryWithFlowable(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Flowable<GithubResponseApi>


    @GET("search/repositories")
    fun searchGithubRepositoryWithFlatMap(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Observable<GithubResponseApi>



    @GET("posts")
    fun getPosts(): Observable<List<Post>>

    @GET("posts/{id}/comments")
    fun getComments(
        @Path("id") id: Int
    ): Observable<List<Comment>>

    @GET("posts/{id}")
    fun getPost(
        @Path("id") id: Int
    ): Observable<Post>

}
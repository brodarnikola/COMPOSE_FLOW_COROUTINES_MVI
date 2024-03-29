package com.example.mvi_compose.general.repositories

import com.example.mvi_compose.general.network.NetworkResult
import com.example.mvi_compose.general.network.data.github.GithubResponseApi
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow


interface GithubRepo {

    suspend fun getGithubRepositories(query: String) : NetworkResult<GithubResponseApi>

    suspend fun getSearchRepositories(query: String) : Flow<GithubResponseApi>

    suspend fun getGithubRepositoriesSharedFlow(query: String) : NetworkResult<GithubResponseApi>


    // practice of rxjava3,, with single and flowable example
    fun getSearchRepositorieRxJava2(query: String, page: Int, perPage: Int) : Single<GithubResponseApi>

    fun getSearchRepositorieWithFlowableRxJava2(query: String) : Flowable<GithubResponseApi>
}
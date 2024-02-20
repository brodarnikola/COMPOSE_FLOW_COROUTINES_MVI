package com.example.mvi_compose.movies.repositories

import com.example.mvi_compose.movies.network.data.github.GithubResponseApi


interface GithubRepo {

    suspend fun getSearchRepositoriesResultStream(query: String) : GithubResponseApi

}
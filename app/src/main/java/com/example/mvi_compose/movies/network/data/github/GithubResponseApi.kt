package com.example.mvi_compose.movies.network.data.github

import com.google.gson.annotations.SerializedName

data class GithubResponseApi(
    @SerializedName("total_count")
    val total_count: Int = 0,
    @SerializedName("incomplete_results")
    val incomplete_results: Boolean = false,
    @SerializedName("items")
    val items: List<RepositoryDetails> = listOf()
)

data class RepositoryDetails(
    val id: Long = 0,
    @SerializedName("owner")
    val ownerApi: RepositoryOwner = RepositoryOwner(""),
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("html_url")
    val html_url: String? = "",
    @SerializedName("language")
    val language: String? = "",
    @SerializedName("stargazers_count")
    val stars: Int = 0,
    @SerializedName("forks_count")
    val forks: Int = 0
)

data class RepositoryOwner(
    @SerializedName("avatar_url")
    val avatarUrl: String = ""
)
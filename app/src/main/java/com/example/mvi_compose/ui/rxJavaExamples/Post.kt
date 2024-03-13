package com.example.mvi_compose.ui.rxJavaExamples

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String? = "",
    val body: String? = "",
    val comments: List<Comment> = listOf()
)

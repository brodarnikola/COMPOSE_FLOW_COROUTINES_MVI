package com.example.mvi_compose.ui.rxJavaExamples

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String?,
    val email: String?,
    val body: String?
)
package com.example.mvi_compose.ui.rxJavaExamples

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun RxJava3ExamplesScreeen(
) {
    val context = LocalContext.current

    context.startActivity(Intent(context, RxJava3ExampleActivity::class.java))

}
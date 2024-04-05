package com.example.mvi_compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvi_compose.R
import com.example.mvi_compose.ui.github_location.GithubLocationViewModel

@Composable
fun MachineLearningRxJava3(
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {

        Text(text = "Other possibilities to explore, to see it", modifier = Modifier.padding(vertical = 10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "Machine learning")
            Image(
                modifier = Modifier.size(30.dp),
                contentDescription = "",
                painter = painterResource(
                    id =  R.drawable.like
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "RxJava3")
            Image(
                modifier = Modifier.size(20.dp),
                contentDescription = "",
                painter = painterResource(
                    id =  R.drawable.like
                )
            )
        }
    }

}


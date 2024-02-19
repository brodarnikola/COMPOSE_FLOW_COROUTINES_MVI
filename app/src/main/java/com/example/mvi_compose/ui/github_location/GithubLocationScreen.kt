package com.example.mvi_compose.ui.github_location

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.R
import com.example.mvi_compose.movies.network.data.Trailer
import com.example.mvi_compose.movies.utils.AppConstants
import com.example.mvi_compose.ui.GithubLocationViewModel
import com.example.mvi_compose.ui.MovieDetailsState
import com.example.mvi_compose.ui.movies.LoadingScreen

@Composable
fun GithubLocationScreen(
    viewModel: GithubLocationViewModel
) {

    val context = LocalContext.current
    val githubLocation = viewModel.state.collectAsStateWithLifecycle().value

    githubLocation.let {
        if (githubLocation.isLoading) LoadingScreen()
//        else if( moviesState.error.isNotEmpty() )
//            ErrorScreen(error = moviesState.error)
        else if (githubLocation.trailers?.isNotEmpty() == true) {
//            MovieDetailsDataScreen(
//                detailsState,
//                navigateUp = navigateUp,
//                onFavClicked = {
//                    viewModel.onEvent(MovieDetailsEvents.UpdateLikeState)
//                }) { trailerKey ->
//                openMovieTrailer(trailerKey, context)
//            }
        }
    }
}

fun openMovieTrailer(trailerKey: String, context: Context) {
    val intent = try {
        Intent(Intent.ACTION_VIEW, Uri.parse("${AppConstants.YOUTUBE_APP_URI}$trailerKey"))
    } catch (ex: ActivityNotFoundException) {
        Intent(Intent.ACTION_VIEW, Uri.parse("${AppConstants.YOUTUBE_WEB_URI}$trailerKey"))
    }
    context.startActivity(intent)
}

@Composable
fun MovieDetailsDataScreen(
    detailsState: MovieDetailsState,
    navigateUp: () -> Unit,
    onFavClicked: () -> Unit,
    onTrailerClick: (trailerKey: String) -> Unit,
) {

    ConstraintLayout(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        val arrowBack = createRef()
        val logo = createRef()
        val title = createRef()
        val release = createRef()
        val votes = createRef()
        val rate = createRef()
        val likeIcon = createRef()
        val plot = createRef()
        val trailersList = createRef()

        val backgroundColor = colorResource(id = R.color.purple_200)
        Image(
            painter = painterResource(id = R.drawable.ic_m3_back_arrow),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .constrainAs(arrowBack) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .drawBehind {
                    drawCircle(backgroundColor)
                }
                .size(40.dp)
                .clickable {
                    navigateUp()
                }
        )

        Image(
            painter = rememberAsyncImagePainter("${BuildConfig.IMAGE_URL}${detailsState.movie?.poster_path}"),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(arrowBack.bottom, margin = 10.dp)
                }
                .width(160.dp)
                .height(135.dp)
                .padding(end = 10.dp)
        )


    }
}

@Composable
fun TrailerList(trailers: List<Trailer>, onTrailerClick: (String) -> Unit) {

    val finalTrailersList = remember { trailers }
    LazyColumn {
        items(
            items = finalTrailersList,
            key = { trailer ->
                trailer.id
            }
        ) { trailer ->
            Row(Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onTrailerClick(trailer.key)
                },
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier
                        .height(35.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth(.9f),
                    text = trailer.name
                )

                Image(
                    modifier = Modifier
                        .padding(2.dp),
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    colorFilter = ColorFilter.tint(Color.Red),
                    contentDescription = "play icon"
                )
            }
        }
    }
}

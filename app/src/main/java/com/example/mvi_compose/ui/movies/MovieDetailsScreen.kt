package com.example.mvi_compose.ui.movies

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.network.data.movie.Trailer
import com.example.mvi_compose.ui.MovieDetailsViewModel
import com.example.mvi_compose.R
import com.example.mvi_compose.movies.utils.AppConstants
import com.example.mvi_compose.ui.MovieDetailsEvents
import com.example.mvi_compose.ui.MovieDetailsState

@Composable
fun MovieDetailsScreen(
    viewModel: MovieDetailsViewModel,
    navigateUp: () -> Unit,
) {

    val context = LocalContext.current
    val detailsState = viewModel.state.collectAsStateWithLifecycle().value

    detailsState.let {
        if (detailsState.isLoading) LoadingScreen()
//        else if( moviesState.error.isNotEmpty() )
//            ErrorScreen(error = moviesState.error)
        else if (detailsState.trailers?.isNotEmpty() == true) {
            MovieDetailsDataScreen(
                detailsState,
                navigateUp = navigateUp,
                onFavClicked = {
                    viewModel.onEvent(MovieDetailsEvents.UpdateLikeState)
                }) { trailerKey ->
                openMovieTrailer(trailerKey, context)
            }
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

        detailsState.movie?.title?.let {
            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(logo.end)
                        top.linkTo(arrowBack.bottom, margin = 10.dp)
//                            top.linkTo(logo.top)
                    },
                text = it,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(release) {
                    start.linkTo(logo.end)
                    top.linkTo(title.bottom)
                }
                .padding(top = 4.dp),
            fontSize = 14.sp,
            text = "Released in : ${detailsState.movie?.release_date}",
            color = Color.LightGray
        )

        Text(
            modifier = Modifier
                .constrainAs(votes) {
                    start.linkTo(logo.end)
                    top.linkTo(release.bottom)
                }
                .padding(top = 4.dp),
            fontSize = 14.sp,
            text = "Votes : ${detailsState.movie?.vote_count}",
            color = Color.LightGray
        )

        Text(
            modifier = Modifier
                .constrainAs(rate) {
                    start.linkTo(logo.end)
                    top.linkTo(votes.bottom)
                }
                .padding(top = 6.dp),
            fontSize = 16.sp,
            text = "${detailsState.movie?.vote_average}",
            color = Color.LightGray
        )

        Image(
            modifier = Modifier
                .constrainAs(likeIcon) {
                    start.linkTo(rate.end, margin = 10.dp)
                    top.linkTo(votes.bottom)
                }
                .size(30.dp)
                .padding(top = 4.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onFavClicked()
                },
            contentDescription = "",
            painter = painterResource(
                id = if (detailsState.isLiked) R.drawable.like
                else R.drawable.dislike
            )
        )

        detailsState.movie?.overview?.let {
            Text(
                modifier = Modifier
                    .constrainAs(plot) {
                        start.linkTo(parent.start)
                        top.linkTo(logo.bottom)
                    }
                    .padding(top = 16.dp),
                text = it,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(trailersList) {
                    start.linkTo(parent.start)
                    top.linkTo(plot.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()) {
            if (detailsState.trailers?.isNotEmpty() == true)
                TrailerList(detailsState.trailers, onTrailerClick)
        }
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

@Preview
@Composable
fun MovieDetailsPreview() {
//    val movieDetailsState = DetailsState(
//        movie =
//        Movie(
//            id = 1,
//            poster_path = "https://picsum.photos/seed/picsum/200/300\n",
//            title = "SpiderMan",
//            release_date = "2021-05-26",
//            vote_average = 8.7,
//            vote_count = 100,
//            original_language = "en",
//            original_title = "",
//            popularity = 88.5,
//            video = true,
//            overview = "Spiderman movies is about man turning into a spider which can fly and attack bad people falling them dead",
//        ),
//        trailers = arrayListOf(
//            Trailer(id = "1", name = "spiderman intro", key = ""),
//            Trailer(id = "1", name = "spiderman highlights", key = ""),
//            Trailer(id = "1", name = "spiderman end", key = ""),
//        ),
//        isLiked = true
//    )
//    MVIComposeSampleTheme {
//        MovieDetailsScreen(state = MutableLiveData(movieDetailsState), {}, {})
//    }
}


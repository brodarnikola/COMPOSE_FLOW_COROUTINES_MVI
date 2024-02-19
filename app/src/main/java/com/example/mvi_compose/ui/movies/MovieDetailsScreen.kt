package com.example.mvi_compose.ui.movies

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.details.Trailer
import com.example.mvi_compose.ui.MovieDetailsViewModel
import com.example.mvi_compose.R
import com.example.mvi_compose.ui.MovieDetailsState

@Composable
fun MovieDetailsScreen(
    viewModel: MovieDetailsViewModel,
    movieId: Long,
//    onFavClicked: () -> Unit,
//    onTrailerClick: (String) -> Unit
) {

    Log.d("MOVIE_ID", "Movie id is 55: ${movieId}")
    val detailsState = viewModel.state.collectAsStateWithLifecycle().value

    detailsState.let {
        if (detailsState.isLoading) LoadingScreen()
//        else if( moviesState.error.isNotEmpty() )
//            ErrorScreen(error = moviesState.error)
        else if (detailsState.trailers?.isNotEmpty() == true) MovieDetailsDataScreen(detailsState) //(movies = detailsState.trailers, viewModel)
    }
}

@Composable
fun MovieDetailsDataScreen(detailsState: MovieDetailsState) {  //(movies: List<Trailer>, viewModel: MovieDetailsViewModel) {

//    val movieDetails = viewModel.state.collectAsStateWithLifecycle()
//    with(detailsState) {
//        ConstraintLayout(
//            androidx.compose.ui.Modifier
//                .fillMaxHeight()
//                .fillMaxWidth()
//                .padding(20.dp)
//        ) {
    Column(
        androidx.compose.ui.Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(20.dp)
    ) {

//            val logo = createRef()
//            val title = createRef()
//            val release = createRef()
//            val votes = createRef()
//            val rate = createRef()
//            val ratingBar = createRef()
//            val likeIcon = createRef()
//            val plot = createRef()
//            val trailersList = createRef()

            Image(
                painter = rememberImagePainter("${BuildConfig.IMAGE_URL}${detailsState.movie?.poster_path}"),
                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                contentDescription = null,
                modifier = androidx.compose.ui.Modifier
//                    .constrainAs(logo) {
//                        start.linkTo(parent.start)
//                        top.linkTo(parent.top)
//                    }
                    .width(160.dp)
                    .height(135.dp)
                    .padding(end = 10.dp)
            )

            detailsState.movie?.title?.let {
                Text(
//                    modifier = androidx.compose.ui.Modifier
//                        .constrainAs(title) {
//                            start.linkTo(logo.end)
//                            top.linkTo(logo.top)
//                        },
                    text = it,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }

            Text(
                modifier = androidx.compose.ui.Modifier
//                    .constrainAs(release) {
//                        start.linkTo(logo.end)
//                        top.linkTo(title.bottom)
//                    }
                    .padding(top = 4.dp),
                fontSize = 14.sp,
                text = "Released in : ${detailsState.movie?.release_date}",
                color =  androidx.compose.ui.graphics.Color.LightGray
            )

            Text(
                modifier = androidx.compose.ui.Modifier
//                    .constrainAs(votes) {
//                        start.linkTo(logo.end)
//                        top.linkTo(release.bottom)
//                    }
                    .padding(top = 4.dp),
                fontSize = 14.sp,
                text = "Votes : ${detailsState.movie?.vote_count}",
                color =  androidx.compose.ui.graphics.Color.LightGray

            )

            Text(
                modifier = androidx.compose.ui.Modifier
//                    .constrainAs(rate) {
//                        start.linkTo(logo.end)
//                        top.linkTo(votes.bottom)
//                    }
                    .padding(top = 6.dp),
                fontSize = 16.sp,
                text = "${detailsState.movie?.vote_average}",
                color =  androidx.compose.ui.graphics.Color.LightGray
            )

//            (movie?.vote_average?.div(2))?.toFloat()?.let {
//                RatingBar(
//                    modifier = Modifier
//                        .constrainAs(ratingBar) {
//                            start.linkTo(rate.end)
//                            top.linkTo(votes.bottom)
//                        }
//                        .padding(start = 4.dp, top = 4.dp),
//                    value = it,
//                    ratingBarStyle = RatingBarStyle.Normal,
//                    onRatingChanged = {},
//                    onValueChange = {},
//                )
//            }

            Image(
                modifier = androidx.compose.ui.Modifier
//                    .constrainAs(likeIcon) {
//                        start.linkTo(logo.end)
//                        bottom.linkTo(logo.bottom)
//                    }
                    .size(30.dp)
                    .padding(top = 4.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
//                        onFavClicked()
                    },
                contentDescription = "",
                painter = painterResource(
                    id = if (detailsState.isLiked) R.drawable.like
                    else R.drawable.dislike
                )
            )

            detailsState.movie?.overview?.let {
                Text(
                    modifier = androidx.compose.ui.Modifier
//                        .constrainAs(plot) {
//                            start.linkTo(parent.start)
//                            top.linkTo(logo.bottom)
//                        }
                        .padding(top = 16.dp),
                    text = it,
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }

            Box(
                modifier = Modifier
//                    .constrainAs(trailersList) {
//                        start.linkTo(parent.start)
//                        top.linkTo(plot.bottom)
//                    }
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()) {
//                if (isLoading) LoadingScreen()
//                errorMessage?.let {
//                    ErrorScreen(error = it)
//                    return@Box
//                }
                if (detailsState.trailers?.isNotEmpty() == true)
                    TrailerList(detailsState.trailers, /*onTrailerClick*/)
            }
        }
//    }
}

@Composable
fun TrailerList(trailers: List<Trailer>, /*onTrailerClick: (String) -> Unit*/) {
    LazyColumn {
        items(
            items = trailers,
            key = { trailer ->
                 trailer.id
            }
        ) { trailer ->
            Row(
                Modifier.fillParentMaxWidth(),
            ) {
//                repeat(trailers.size) {
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        TrailerItem(
                            trailer,
//                            onTrailerClick
                        )
                    }
//                }
            }
        }
    }
}

@Composable
fun TrailerItem(
    trailer: Trailer,
//    onTrailerClick: (String) -> Unit
) {
    ConstraintLayout {
        val name = createRef()
        val icon = createRef()

        Text(
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .height(35.dp)
                .wrapContentHeight(Alignment.CenterVertically)
                .fillMaxWidth(.9f),
            text = trailer.name
        )

        Image(
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(name.end)
                }
                .padding(2.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
//                    onTrailerClick(trailer.key)
                },
            painter = painterResource(id = android.R.drawable.ic_media_play),
            colorFilter = ColorFilter.tint(Color.Red),
            contentDescription = "play icon"
        )

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

